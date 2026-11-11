/*
 * sha256.c — SHA-256 雜湊函式（公有領域參考實作）
 *
 * 依 FIPS 180-4 規範實作。程式碼結構清晰、避免 VLA 與 C11 特性，
 * 以確保各 MCU 廠商 toolchain 對 C99 的相容性。
 */
#include "sha256.h"
#include <string.h>   /* memcpy、memset */

/* 32 位元右旋轉（circular right rotate） */
static uint32_t sha256_rotr(uint32_t x, uint32_t n)
{
    /* 以位移組合出旋轉；0xFFFFFFFF 遮罩確保停留在 32 位元 */
    return ((x >> n) | (x << (32 - n))) & 0xFFFFFFFFu;
}

/* SHA-256 使用的 64 個回合常數（取自前 64 個質數立方根小數部位） */
static const uint32_t SHA256_K[64] = {
    0x428a2f98u, 0x71374491u, 0xb5c0fbcfu, 0xe9b5dba5u,
    0x3956c25bu, 0x59f111f1u, 0x923f82a4u, 0xab1c5ed5u,
    0xd807aa98u, 0x12835b01u, 0x243185beu, 0x550c7dc3u,
    0x72be5d74u, 0x80deb1feu, 0x9bdc06a7u, 0xc19bf174u,
    0xe49b69c1u, 0xefbe4786u, 0x0fc19dc6u, 0x240ca1ccu,
    0x2de92c6fu, 0x4a7484aau, 0x5cb0a9dcu, 0x76f988dau,
    0x983e5152u, 0xa831c66du, 0xb00327c8u, 0xbf597fc7u,
    0xc6e00bf3u, 0xd5a79147u, 0x06ca6351u, 0x14292967u,
    0x27b70a85u, 0x2e1b2138u, 0x4d2c6dfcu, 0x53380d13u,
    0x650a7354u, 0x766a0abbu, 0x81c2c92eu, 0x92722c85u,
    0xa2bfe8a1u, 0xa81a664bu, 0xc24b8b70u, 0xc76c51a3u,
    0xd192e819u, 0xd6990624u, 0xf40e3585u, 0x106aa070u,
    0x19a4c116u, 0x1e376c08u, 0x2748774cu, 0x34b0bcb5u,
    0x391c0cb3u, 0x4ed8aa4au, 0x5b9cca4fu, 0x682e6ff3u,
    0x748f82eeu, 0x78a5636fu, 0x84c87814u, 0x8cc70208u,
    0x90befffau, 0xa4506cebu, 0xbef9a3f7u, 0xc67178f2u
};

/*
 * 處理單一 64 位元組區塊，更新八個狀態暫存器
 * @param state 八個狀態暫存器
 * @param block 待處理的 64 位元組區塊
 */
static void sha256_transform(uint32_t state[8], const uint8_t block[SHA256_BLOCK_LEN])
{
    uint32_t w[64];                          /* 訊息排程（message schedule） */
    uint32_t a, b, c, d, e, f, g, h;         /* 八個工作暫存器 */
    uint32_t t1, t2;                         /* 中間運算暫存 */
    uint32_t s0, s1, ch, maj;                /* 中間函式結果 */
    size_t i;                                /* 迴圈索引 */

    /* 步驟一：將 16 個大端序（big-endian）字組載入 w[0..15] */
    for (i = 0; i < 16; i++) {
        w[i] = ((uint32_t)block[i * 4] << 24)
             | ((uint32_t)block[i * 4 + 1] << 16)
             | ((uint32_t)block[i * 4 + 2] << 8)
             | ((uint32_t)block[i * 4 + 3]);
    }

    /* 步驟二：擴展出 w[16..63] */
    for (i = 16; i < 64; i++) {
        /* 小 sigma0 與小 sigma1 擴散函式 */
        s0 = sha256_rotr(w[i - 15], 7) ^ sha256_rotr(w[i - 15], 18) ^ (w[i - 15] >> 3);
        s1 = sha256_rotr(w[i - 2], 17) ^ sha256_rotr(w[i - 2], 19) ^ (w[i - 2] >> 10);
        w[i] = (w[i - 16] + s0 + w[i - 7] + s1) & 0xFFFFFFFFu;
    }

    /* 步驟三：載入目前狀態到工作暫存器 */
    a = state[0]; b = state[1]; c = state[2]; d = state[3];
    e = state[4]; f = state[5]; g = state[6]; h = state[7];

    /* 步驟四：主壓縮迴圈，共 64 回合 */
    for (i = 0; i < 64; i++) {
        /* 大 sigma1 與 choose 函式 */
        s1 = sha256_rotr(e, 6) ^ sha256_rotr(e, 11) ^ sha256_rotr(e, 25);
        ch = (e & f) ^ ((~e) & g);
        t1 = (h + s1 + ch + SHA256_K[i] + w[i]) & 0xFFFFFFFFu;

        /* 大 sigma0 與 majority 函式 */
        s0 = sha256_rotr(a, 2) ^ sha256_rotr(a, 13) ^ sha256_rotr(a, 22);
        maj = (a & b) ^ (a & c) ^ (b & c);
        t2 = (s0 + maj) & 0xFFFFFFFFu;

        /* 更新工作暫存器 */
        h = g; g = f; f = e;
        e = (d + t1) & 0xFFFFFFFFu;
        d = c; c = b; b = a;
        a = (t1 + t2) & 0xFFFFFFFFu;
    }

    /* 步驟五：把壓縮結果加回狀態暫存器 */
    state[0] = (state[0] + a) & 0xFFFFFFFFu;
    state[1] = (state[1] + b) & 0xFFFFFFFFu;
    state[2] = (state[2] + c) & 0xFFFFFFFFu;
    state[3] = (state[3] + d) & 0xFFFFFFFFu;
    state[4] = (state[4] + e) & 0xFFFFFFFFu;
    state[5] = (state[5] + f) & 0xFFFFFFFFu;
    state[6] = (state[6] + g) & 0xFFFFFFFFu;
    state[7] = (state[7] + h) & 0xFFFFFFFFu;
}

/* 初始化上下文，載入 SHA-256 標準初始雜湊值 */
void sha256_init(sha256_ctx *ctx)
{
    /* 八個初始雜湊值（取自前 8 個質數平方根小數部位） */
    ctx->state[0] = 0x6a09e667u;
    ctx->state[1] = 0xbb67ae85u;
    ctx->state[2] = 0x3c6ef372u;
    ctx->state[3] = 0xa54ff53au;
    ctx->state[4] = 0x510e527fu;
    ctx->state[5] = 0x9b05688cu;
    ctx->state[6] = 0x1f83d9abu;
    ctx->state[7] = 0x5be0cd19u;
    ctx->bitlen = 0;      /* 尚未處理任何位元 */
    ctx->buffer_len = 0;  /* 暫存區為空 */
}

/* 餵入資料，湊滿 64 位元組即進行一次區塊轉換 */
void sha256_update(sha256_ctx *ctx, const uint8_t *data, size_t len)
{
    size_t i;  /* 逐位元組處理的索引 */

    /* 逐位元組放入暫存區，避免對齊與長度假設 */
    for (i = 0; i < len; i++) {
        ctx->buffer[ctx->buffer_len] = data[i];
        ctx->buffer_len++;

        /* 暫存區湊滿一個區塊就進行轉換並清空 */
        if (ctx->buffer_len == SHA256_BLOCK_LEN) {
            sha256_transform(ctx->state, ctx->buffer);
            ctx->bitlen += 512;   /* 一個區塊為 512 位元 */
            ctx->buffer_len = 0;
        }
    }
}

/* 進行填充（padding）並輸出最終摘要 */
void sha256_final(sha256_ctx *ctx, uint8_t digest[SHA256_DIGEST_LEN])
{
    uint64_t total_bits;  /* 訊息總位元數 */
    size_t i;             /* 迴圈索引 */

    /* 把尚未計入的暫存位元加進總位元數 */
    total_bits = ctx->bitlen + (uint64_t)ctx->buffer_len * 8u;

    /* 步驟一：附加 0x80 起始填充位元組 */
    ctx->buffer[ctx->buffer_len] = 0x80;
    ctx->buffer_len++;

    /* 步驟二：若剩餘空間不足以容納 8 位元組長度欄，先補零並轉換 */
    if (ctx->buffer_len > 56) {
        while (ctx->buffer_len < SHA256_BLOCK_LEN) {
            ctx->buffer[ctx->buffer_len] = 0x00;
            ctx->buffer_len++;
        }
        sha256_transform(ctx->state, ctx->buffer);
        ctx->buffer_len = 0;
    }

    /* 步驟三：補零直到剩下最後 8 位元組長度欄 */
    while (ctx->buffer_len < 56) {
        ctx->buffer[ctx->buffer_len] = 0x00;
        ctx->buffer_len++;
    }

    /* 步驟四：以大端序寫入 64 位元總長度 */
    for (i = 0; i < 8; i++) {
        ctx->buffer[56 + i] = (uint8_t)((total_bits >> (56 - i * 8)) & 0xFFu);
    }
    sha256_transform(ctx->state, ctx->buffer);

    /* 步驟五：把八個狀態暫存器以大端序輸出成 32 位元組摘要 */
    for (i = 0; i < 8; i++) {
        digest[i * 4]     = (uint8_t)((ctx->state[i] >> 24) & 0xFFu);
        digest[i * 4 + 1] = (uint8_t)((ctx->state[i] >> 16) & 0xFFu);
        digest[i * 4 + 2] = (uint8_t)((ctx->state[i] >> 8) & 0xFFu);
        digest[i * 4 + 3] = (uint8_t)(ctx->state[i] & 0xFFu);
    }

    /* 清零上下文，避免中間狀態殘留記憶體 */
    memset(ctx, 0, sizeof(*ctx));
}

/* 一次性計算便利函式 */
void sha256(const uint8_t *data, size_t len, uint8_t digest[SHA256_DIGEST_LEN])
{
    sha256_ctx ctx;          /* 區域上下文，函式結束即釋放 */
    sha256_init(&ctx);       /* 初始化 */
    sha256_update(&ctx, data, len);  /* 餵入資料 */
    sha256_final(&ctx, digest);      /* 輸出摘要 */
}
