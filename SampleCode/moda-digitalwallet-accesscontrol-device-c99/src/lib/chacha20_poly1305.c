/*
 * chacha20_poly1305.c — ChaCha20-Poly1305 AEAD（RFC 8439）
 *
 * ChaCha20 串流密碼 + Poly1305 單次驗證碼。Poly1305 採用公有領域的
 * poly1305-donna 26 位元 limb 演算法改寫，避免 128 位元整數依賴，
 * 以確保各 MCU toolchain 對 C99 的相容性。
 */
#include "chacha20_poly1305.h"
#include <string.h>   /* memcpy、memset */

/* ---------- ChaCha20 區塊函式（RFC 8439 §2.3） ---------- */

/* 32 位元左旋轉 */
static uint32_t chacha_rotl(uint32_t x, uint32_t n)
{
    return ((x << n) | (x >> (32 - n))) & 0xFFFFFFFFu;
}

/* ChaCha 四分之一回合（quarter round），就地更新四個狀態字 */
static void chacha_quarter_round(uint32_t *x, int a, int b, int c, int d)
{
    /* 依 RFC 8439 定義的加法—互斥或—旋轉序列 */
    x[a] = (x[a] + x[b]) & 0xFFFFFFFFu; x[d] ^= x[a]; x[d] = chacha_rotl(x[d], 16);
    x[c] = (x[c] + x[d]) & 0xFFFFFFFFu; x[b] ^= x[c]; x[b] = chacha_rotl(x[b], 12);
    x[a] = (x[a] + x[b]) & 0xFFFFFFFFu; x[d] ^= x[a]; x[d] = chacha_rotl(x[d], 8);
    x[c] = (x[c] + x[d]) & 0xFFFFFFFFu; x[b] ^= x[c]; x[b] = chacha_rotl(x[b], 7);
}

/* 從小端序位元組讀入 32 位元字 */
static uint32_t chacha_load32_le(const uint8_t *p)
{
    return (uint32_t)p[0]
         | ((uint32_t)p[1] << 8)
         | ((uint32_t)p[2] << 16)
         | ((uint32_t)p[3] << 24);
}

/*
 * 產生單一 64 位元組 ChaCha20 keystream 區塊
 * @param key     32 位元組金鑰
 * @param counter 區塊計數器
 * @param nonce   12 位元組 nonce
 * @param out     輸出 64 位元組 keystream
 */
static void chacha20_block(const uint8_t key[CHACHA20_KEY_LEN], uint32_t counter,
                           const uint8_t nonce[CHACHA20_NONCE_LEN], uint8_t out[64])
{
    uint32_t state[16];    /* 初始狀態矩陣 */
    uint32_t working[16];  /* 20 回合運算用的工作矩陣 */
    int i;                 /* 迴圈索引 */

    /* 前四個字為固定常數 "expand 32-byte k" */
    state[0] = 0x61707865u;
    state[1] = 0x3320646eu;
    state[2] = 0x79622d32u;
    state[3] = 0x6b206574u;
    /* 接著八個字為 256 位元金鑰（小端序） */
    for (i = 0; i < 8; i++) {
        state[4 + i] = chacha_load32_le(key + i * 4);
    }
    /* 第 12 字為區塊計數器 */
    state[12] = counter;
    /* 最後三個字為 96 位元 nonce（小端序） */
    state[13] = chacha_load32_le(nonce + 0);
    state[14] = chacha_load32_le(nonce + 4);
    state[15] = chacha_load32_le(nonce + 8);

    /* 複製一份做 20 回合（10 次雙回合）運算 */
    for (i = 0; i < 16; i++) {
        working[i] = state[i];
    }
    for (i = 0; i < 10; i++) {
        /* 直行回合（column rounds） */
        chacha_quarter_round(working, 0, 4, 8, 12);
        chacha_quarter_round(working, 1, 5, 9, 13);
        chacha_quarter_round(working, 2, 6, 10, 14);
        chacha_quarter_round(working, 3, 7, 11, 15);
        /* 對角回合（diagonal rounds） */
        chacha_quarter_round(working, 0, 5, 10, 15);
        chacha_quarter_round(working, 1, 6, 11, 12);
        chacha_quarter_round(working, 2, 7, 8, 13);
        chacha_quarter_round(working, 3, 4, 9, 14);
    }

    /* 工作矩陣加回初始狀態，並以小端序輸出 */
    for (i = 0; i < 16; i++) {
        uint32_t v = (working[i] + state[i]) & 0xFFFFFFFFu;
        out[i * 4]     = (uint8_t)(v & 0xFF);
        out[i * 4 + 1] = (uint8_t)((v >> 8) & 0xFF);
        out[i * 4 + 2] = (uint8_t)((v >> 16) & 0xFF);
        out[i * 4 + 3] = (uint8_t)((v >> 24) & 0xFF);
    }
}

/* ChaCha20 串流加密：以 keystream 對輸入做 XOR */
void chacha20_xor(const uint8_t key[CHACHA20_KEY_LEN], uint32_t counter,
                  const uint8_t nonce[CHACHA20_NONCE_LEN],
                  const uint8_t *in, size_t len, uint8_t *out)
{
    uint8_t block[64];  /* 單一 keystream 區塊 */
    size_t i;           /* 已處理位元組數 */
    size_t j;           /* 區塊內索引 */

    i = 0;
    while (i < len) {
        /* 產生對應計數器的 keystream 區塊 */
        chacha20_block(key, counter, nonce, block);
        counter++;   /* 計數器逐區塊遞增 */

        /* 對本區塊範圍逐位元組做 XOR */
        for (j = 0; j < 64 && i < len; j++, i++) {
            out[i] = (uint8_t)(in[i] ^ block[j]);
        }
    }

    /* 清零暫存 keystream */
    memset(block, 0, sizeof(block));
}

/* ---------- Poly1305 單次驗證碼（RFC 8439 §2.5） ---------- */

/*
 * Poly1305 上下文，採 5 個 26 位元 limb 表示 130 位元運算元
 */
typedef struct {
    uint32_t r[5];       /* 已裁切（clamp）的金鑰 r */
    uint32_t h[5];       /* 累加器 */
    uint32_t pad[4];     /* 最後加上的 s */
    uint8_t  buffer[16]; /* 未滿一個區塊的暫存 */
    size_t   leftover;   /* 暫存位元組數 */
} poly1305_ctx;

/* 從小端序位元組讀 32 位元 */
static uint32_t poly_load32_le(const uint8_t *p)
{
    return (uint32_t)p[0]
         | ((uint32_t)p[1] << 8)
         | ((uint32_t)p[2] << 16)
         | ((uint32_t)p[3] << 24);
}

/* 以 32 位元組金鑰初始化 Poly1305 */
static void poly1305_init(poly1305_ctx *ctx, const uint8_t key[32])
{
    uint32_t t0, t1, t2, t3;  /* 讀入的四個金鑰字 */

    /* 讀入前 16 位元組作為 r，並依規範裁切特定位元 */
    t0 = poly_load32_le(key + 0);
    t1 = poly_load32_le(key + 4);
    t2 = poly_load32_le(key + 8);
    t3 = poly_load32_le(key + 12);

    /* 將 r 切成 5 個 26 位元 limb，同時套用 clamp 遮罩 */
    ctx->r[0] = (t0) & 0x3ffffff;
    ctx->r[1] = ((t0 >> 26) | (t1 << 6)) & 0x3ffff03;
    ctx->r[2] = ((t1 >> 20) | (t2 << 12)) & 0x3ffc0ff;
    ctx->r[3] = ((t2 >> 14) | (t3 << 18)) & 0x3f03fff;
    ctx->r[4] = ((t3 >> 8)) & 0x00fffff;

    /* 累加器歸零 */
    ctx->h[0] = 0; ctx->h[1] = 0; ctx->h[2] = 0; ctx->h[3] = 0; ctx->h[4] = 0;

    /* 後 16 位元組作為最終要加上的 s */
    ctx->pad[0] = poly_load32_le(key + 16);
    ctx->pad[1] = poly_load32_le(key + 20);
    ctx->pad[2] = poly_load32_le(key + 24);
    ctx->pad[3] = poly_load32_le(key + 28);

    ctx->leftover = 0;
}

/*
 * 處理若干完整 16 位元組區塊
 * @param ctx    上下文
 * @param m      輸入資料
 * @param bytes  資料長度（必為 16 的倍數，最後一段除外）
 * @param is_final 是否為最後一段（影響最高位補位）
 */
static void poly1305_blocks(poly1305_ctx *ctx, const uint8_t *m, size_t bytes, int is_final)
{
    const uint32_t hibit = is_final ? 0 : (1u << 24);  /* 完整區塊在第 128 位補 1 */
    uint32_t r0, r1, r2, r3, r4;   /* 金鑰 limb */
    uint32_t s1, s2, s3, s4;       /* r 的 5 倍預算 */
    uint32_t h0, h1, h2, h3, h4;   /* 累加器 limb */
    uint64_t d0, d1, d2, d3, d4;   /* 乘法中間結果 */
    uint32_t c;                    /* 進位 */

    r0 = ctx->r[0]; r1 = ctx->r[1]; r2 = ctx->r[2]; r3 = ctx->r[3]; r4 = ctx->r[4];
    s1 = r1 * 5; s2 = r2 * 5; s3 = r3 * 5; s4 = r4 * 5;
    h0 = ctx->h[0]; h1 = ctx->h[1]; h2 = ctx->h[2]; h3 = ctx->h[3]; h4 = ctx->h[4];

    while (bytes >= 16) {
        /* 讀入一個 16 位元組區塊為 5 個 26 位元 limb，並補最高位 */
        h0 += (poly_load32_le(m + 0)) & 0x3ffffff;
        h1 += ((poly_load32_le(m + 3) >> 2)) & 0x3ffffff;
        h2 += ((poly_load32_le(m + 6) >> 4)) & 0x3ffffff;
        h3 += ((poly_load32_le(m + 9) >> 6)) & 0x3ffffff;
        h4 += ((poly_load32_le(m + 12) >> 8)) | hibit;

        /* h = h * r  mod 2^130-5，先做 schoolbook 乘法 */
        d0 = (uint64_t)h0 * r0 + (uint64_t)h1 * s4 + (uint64_t)h2 * s3 + (uint64_t)h3 * s2 + (uint64_t)h4 * s1;
        d1 = (uint64_t)h0 * r1 + (uint64_t)h1 * r0 + (uint64_t)h2 * s4 + (uint64_t)h3 * s3 + (uint64_t)h4 * s2;
        d2 = (uint64_t)h0 * r2 + (uint64_t)h1 * r1 + (uint64_t)h2 * r0 + (uint64_t)h3 * s4 + (uint64_t)h4 * s3;
        d3 = (uint64_t)h0 * r3 + (uint64_t)h1 * r2 + (uint64_t)h2 * r1 + (uint64_t)h3 * r0 + (uint64_t)h4 * s4;
        d4 = (uint64_t)h0 * r4 + (uint64_t)h1 * r3 + (uint64_t)h2 * r2 + (uint64_t)h3 * r1 + (uint64_t)h4 * r0;

        /* 逐 limb 進位歸約 */
        c = (uint32_t)(d0 >> 26); h0 = (uint32_t)d0 & 0x3ffffff;
        d1 += c; c = (uint32_t)(d1 >> 26); h1 = (uint32_t)d1 & 0x3ffffff;
        d2 += c; c = (uint32_t)(d2 >> 26); h2 = (uint32_t)d2 & 0x3ffffff;
        d3 += c; c = (uint32_t)(d3 >> 26); h3 = (uint32_t)d3 & 0x3ffffff;
        d4 += c; c = (uint32_t)(d4 >> 26); h4 = (uint32_t)d4 & 0x3ffffff;
        /* 2^130 ≡ 5，故最高進位乘 5 折回最低 limb */
        h0 += c * 5; c = h0 >> 26; h0 &= 0x3ffffff;
        h1 += c;

        m += 16;
        bytes -= 16;
    }

    ctx->h[0] = h0; ctx->h[1] = h1; ctx->h[2] = h2; ctx->h[3] = h3; ctx->h[4] = h4;
}

/* 餵入任意長度資料，內部自動累積至完整區塊 */
static void poly1305_update(poly1305_ctx *ctx, const uint8_t *m, size_t bytes)
{
    size_t i;  /* 迴圈索引 */

    /* 先補滿先前殘留的區塊 */
    if (ctx->leftover > 0) {
        size_t want = 16 - ctx->leftover;
        if (want > bytes) {
            want = bytes;
        }
        for (i = 0; i < want; i++) {
            ctx->buffer[ctx->leftover + i] = m[i];
        }
        ctx->leftover += want;
        m += want;
        bytes -= want;
        /* 湊滿 16 位元組才處理 */
        if (ctx->leftover < 16) {
            return;
        }
        poly1305_blocks(ctx, ctx->buffer, 16, 0);
        ctx->leftover = 0;
    }

    /* 處理整數個完整區塊 */
    if (bytes >= 16) {
        size_t want = bytes & ~((size_t)15);
        poly1305_blocks(ctx, m, want, 0);
        m += want;
        bytes -= want;
    }

    /* 其餘不足一區塊者暫存 */
    for (i = 0; i < bytes; i++) {
        ctx->buffer[ctx->leftover + i] = m[i];
    }
    ctx->leftover += bytes;
}

/* 結束運算並輸出 16 位元組標籤 */
static void poly1305_finish(poly1305_ctx *ctx, uint8_t mac[16])
{
    uint32_t h0, h1, h2, h3, h4;   /* 累加器 */
    uint32_t c;                    /* 進位 */
    uint32_t g0, g1, g2, g3, g4;   /* h - p 的候選值 */
    uint32_t mask;                 /* 常數時間選擇遮罩 */
    uint64_t f;                    /* 加 pad 的中間值 */

    /* 若有殘留區塊，補 0x01 後以「最後段」模式處理 */
    if (ctx->leftover > 0) {
        size_t i = ctx->leftover;
        ctx->buffer[i++] = 1;
        for (; i < 16; i++) {
            ctx->buffer[i] = 0;
        }
        poly1305_blocks(ctx, ctx->buffer, 16, 1);
    }

    h0 = ctx->h[0]; h1 = ctx->h[1]; h2 = ctx->h[2]; h3 = ctx->h[3]; h4 = ctx->h[4];

    /* 完整進位歸約 */
    c = h1 >> 26; h1 &= 0x3ffffff;
    h2 += c; c = h2 >> 26; h2 &= 0x3ffffff;
    h3 += c; c = h3 >> 26; h3 &= 0x3ffffff;
    h4 += c; c = h4 >> 26; h4 &= 0x3ffffff;
    h0 += c * 5; c = h0 >> 26; h0 &= 0x3ffffff;
    h1 += c;

    /* 計算 h - (2^130 - 5)，用以判斷是否需要最終減法 */
    g0 = h0 + 5; c = g0 >> 26; g0 &= 0x3ffffff;
    g1 = h1 + c; c = g1 >> 26; g1 &= 0x3ffffff;
    g2 = h2 + c; c = g2 >> 26; g2 &= 0x3ffffff;
    g3 = h3 + c; c = g3 >> 26; g3 &= 0x3ffffff;
    g4 = h4 + c - (1u << 26);

    /* 依最高位決定採用 h 或 h-p（常數時間選擇） */
    mask = (g4 >> 31) - 1;   /* g4 為負則 mask=0，否則 mask=全1 */
    g0 &= mask; g1 &= mask; g2 &= mask; g3 &= mask; g4 &= mask;
    mask = ~mask;
    h0 = (h0 & mask) | g0;
    h1 = (h1 & mask) | g1;
    h2 = (h2 & mask) | g2;
    h3 = (h3 & mask) | g3;
    h4 = (h4 & mask) | g4;

    /* 把 5 個 26 位元 limb 重組為 4 個 32 位元字 */
    h0 = (h0 | (h1 << 26)) & 0xffffffff;
    h1 = ((h1 >> 6) | (h2 << 20)) & 0xffffffff;
    h2 = ((h2 >> 12) | (h3 << 14)) & 0xffffffff;
    h3 = ((h3 >> 18) | (h4 << 8)) & 0xffffffff;

    /* 加上 pad（即金鑰後半 s） */
    f = (uint64_t)h0 + ctx->pad[0]; h0 = (uint32_t)f;
    f = (uint64_t)h1 + ctx->pad[1] + (f >> 32); h1 = (uint32_t)f;
    f = (uint64_t)h2 + ctx->pad[2] + (f >> 32); h2 = (uint32_t)f;
    f = (uint64_t)h3 + ctx->pad[3] + (f >> 32); h3 = (uint32_t)f;

    /* 以小端序輸出 16 位元組標籤 */
    mac[0] = (uint8_t)(h0); mac[1] = (uint8_t)(h0 >> 8); mac[2] = (uint8_t)(h0 >> 16); mac[3] = (uint8_t)(h0 >> 24);
    mac[4] = (uint8_t)(h1); mac[5] = (uint8_t)(h1 >> 8); mac[6] = (uint8_t)(h1 >> 16); mac[7] = (uint8_t)(h1 >> 24);
    mac[8] = (uint8_t)(h2); mac[9] = (uint8_t)(h2 >> 8); mac[10] = (uint8_t)(h2 >> 16); mac[11] = (uint8_t)(h2 >> 24);
    mac[12] = (uint8_t)(h3); mac[13] = (uint8_t)(h3 >> 8); mac[14] = (uint8_t)(h3 >> 16); mac[15] = (uint8_t)(h3 >> 24);

    /* 清零上下文 */
    memset(ctx, 0, sizeof(*ctx));
}

/* ---------- AEAD 組合（RFC 8439 §2.8） ---------- */

/*
 * 依 RFC 8439 建構 Poly1305 訊息並計算標籤
 * 訊息 = aad || pad16 || ciphertext || pad16 || len(aad) || len(ciphertext)
 */
static void poly1305_compute_tag(const uint8_t poly_key[32],
                                 const uint8_t *aad, size_t aad_len,
                                 const uint8_t *ciphertext, size_t ct_len,
                                 uint8_t tag[POLY1305_TAG_LEN])
{
    poly1305_ctx ctx;          /* Poly1305 上下文 */
    uint8_t pad_block[16];     /* 16 位元組對齊用的零填充 */
    uint8_t length_block[16];  /* 尾端的長度欄位 */
    size_t pad_len;            /* 需補的零位元組數 */
    int i;                     /* 迴圈索引 */

    memset(pad_block, 0, sizeof(pad_block));
    poly1305_init(&ctx, poly_key);

    /* 1) 餵入 AAD 並補齊到 16 的倍數 */
    poly1305_update(&ctx, aad, aad_len);
    pad_len = (16 - (aad_len % 16)) % 16;
    if (pad_len > 0) {
        poly1305_update(&ctx, pad_block, pad_len);
    }

    /* 2) 餵入密文並補齊到 16 的倍數 */
    poly1305_update(&ctx, ciphertext, ct_len);
    pad_len = (16 - (ct_len % 16)) % 16;
    if (pad_len > 0) {
        poly1305_update(&ctx, pad_block, pad_len);
    }

    /* 3) 尾端附上 aad 長度與密文長度（各 8 位元組小端序） */
    for (i = 0; i < 8; i++) {
        length_block[i] = (uint8_t)(((uint64_t)aad_len >> (i * 8)) & 0xFF);
    }
    for (i = 0; i < 8; i++) {
        length_block[8 + i] = (uint8_t)(((uint64_t)ct_len >> (i * 8)) & 0xFF);
    }
    poly1305_update(&ctx, length_block, 16);

    /* 輸出標籤 */
    poly1305_finish(&ctx, tag);
}

/* 產生 Poly1305 一次性金鑰：ChaCha20 counter=0 的前 32 位元組 */
static void derive_poly_key(const uint8_t key[CHACHA20_KEY_LEN],
                            const uint8_t nonce[CHACHA20_NONCE_LEN],
                            uint8_t poly_key[32])
{
    uint8_t block[64];  /* counter=0 的 keystream 區塊 */
    int i;              /* 迴圈索引 */

    chacha20_block(key, 0, nonce, block);
    for (i = 0; i < 32; i++) {
        poly_key[i] = block[i];
    }
    memset(block, 0, sizeof(block));
}

/* 常數時間比較兩個 16 位元組標籤 */
static int tag_equal(const uint8_t a[POLY1305_TAG_LEN], const uint8_t b[POLY1305_TAG_LEN])
{
    uint8_t diff;  /* 差異累積 */
    int i;         /* 迴圈索引 */

    diff = 0;
    for (i = 0; i < POLY1305_TAG_LEN; i++) {
        diff |= (uint8_t)(a[i] ^ b[i]);
    }
    /* diff 為 0 才代表相等 */
    return diff == 0 ? 1 : 0;
}

/* AEAD 認證解密 */
int chacha20_poly1305_decrypt(const uint8_t key[CHACHA20_KEY_LEN],
                              const uint8_t nonce[CHACHA20_NONCE_LEN],
                              const uint8_t *aad, size_t aad_len,
                              const uint8_t *ciphertext, size_t ct_len,
                              const uint8_t tag[POLY1305_TAG_LEN],
                              uint8_t *plaintext)
{
    uint8_t poly_key[32];             /* Poly1305 一次性金鑰 */
    uint8_t expected_tag[POLY1305_TAG_LEN];  /* 重算出的標籤 */

    /* 步驟一：導出 Poly1305 金鑰並重算標籤 */
    derive_poly_key(key, nonce, poly_key);
    poly1305_compute_tag(poly_key, aad, aad_len, ciphertext, ct_len, expected_tag);

    /* 步驟二：標籤不符則拒絕，且不輸出任何明文 */
    if (tag_equal(expected_tag, tag) == 0) {
        memset(poly_key, 0, sizeof(poly_key));
        memset(expected_tag, 0, sizeof(expected_tag));
        return -1;
    }

    /* 步驟三：驗證通過才以 counter=1 解密 */
    chacha20_xor(key, 1, nonce, ciphertext, ct_len, plaintext);

    /* 清零暫存 */
    memset(poly_key, 0, sizeof(poly_key));
    memset(expected_tag, 0, sizeof(expected_tag));
    return 0;
}
