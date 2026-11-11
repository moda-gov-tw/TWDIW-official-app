/*
 * x25519.c — X25519 橢圓曲線 Diffie-Hellman（RFC 7748）
 *
 * 本體移植自公有領域 TweetNaCl 的 crypto_scalarmult，
 * 以 16 個 16 位元 limb（型別 int64_t）表示 GF(2^255-19) 場元素，
 * 採 Montgomery ladder 進行常數步數的純量乘法。
 * 僅改寫命名與註解，演算法與原始碼一致以確保正確性。
 */
#include "x25519.h"
#include <string.h>   /* memset */

/* 場元素型別：16 個 limb，每個 limb 承載 16 位元 */
typedef int64_t gf[16];

/* 常數 121665，用於 Montgomery 曲線的 (A-2)/4 係數 */
static const gf GF_121665 = { 0xDB41, 1 };

/* 進位歸約：把每個 limb 收斂回 16 位元，並處理 2^255-19 折返 */
static void carry25519(gf o)
{
    int i;       /* 迴圈索引 */
    int64_t c;   /* 進位值 */

    for (i = 0; i < 16; i++) {
        o[i] += (int64_t)1 << 16;              /* 加偏移避免負數右移 */
        c = o[i] >> 16;                        /* 取出進位 */
        /* 進位傳遞至下一 limb；最高 limb 依 2^255≡19 規則折回最低 limb（37=2*19-1 已含偏移） */
        o[(i + 1) * (i < 15)] += c - 1 + 37 * (c - 1) * (i == 15);
        o[i] -= c << 16;                       /* 扣掉已進位部分 */
    }
}

/* 常數時間條件交換：b=1 時交換 p 與 q */
static void swap25519(gf p, gf q, int b)
{
    int64_t t;                   /* 暫存交換值 */
    int64_t c = ~((int64_t)b - 1); /* b=1→全1，b=0→全0 */
    int i;                       /* 迴圈索引 */

    for (i = 0; i < 16; i++) {
        t = c & (p[i] ^ q[i]);   /* 僅在遮罩為全1時取得差異 */
        p[i] ^= t;
        q[i] ^= t;
    }
}

/* 從 32 位元組小端序讀入場元素，並清除最高位 */
static void unpack25519(gf o, const uint8_t *n)
{
    int i;  /* 迴圈索引 */

    for (i = 0; i < 16; i++) {
        o[i] = (int64_t)n[2 * i] + ((int64_t)n[2 * i + 1] << 8);
    }
    o[15] &= 0x7fff;  /* 依 RFC 7748 清除最高位 */
}

/* 把場元素完整歸約並輸出成 32 位元組小端序 */
static void pack25519(uint8_t *o, const gf n)
{
    int i, j, b;   /* 迴圈索引與借位旗標 */
    gf m, t;       /* 中間場元素 */

    for (i = 0; i < 16; i++) {
        t[i] = n[i];
    }
    /* 連續三次進位確保收斂 */
    carry25519(t);
    carry25519(t);
    carry25519(t);

    /* 進行兩輪條件減 p，確保結果落在 [0, p) */
    for (j = 0; j < 2; j++) {
        m[0] = t[0] - 0xffed;
        for (i = 1; i < 15; i++) {
            m[i] = t[i] - 0xffff - ((m[i - 1] >> 16) & 1);
            m[i - 1] &= 0xffff;
        }
        m[15] = t[15] - 0x7fff - ((m[14] >> 16) & 1);
        b = (int)((m[15] >> 16) & 1);
        m[14] &= 0xffff;
        swap25519(t, m, 1 - b);
    }

    /* 逐 limb 輸出小端序位元組 */
    for (i = 0; i < 16; i++) {
        o[2 * i]     = (uint8_t)(t[i] & 0xff);
        o[2 * i + 1] = (uint8_t)(t[i] >> 8);
    }
}

/* 場加法 */
static void field_add(gf o, const gf a, const gf b)
{
    int i;  /* 迴圈索引 */
    for (i = 0; i < 16; i++) {
        o[i] = a[i] + b[i];
    }
}

/* 場減法 */
static void field_sub(gf o, const gf a, const gf b)
{
    int i;  /* 迴圈索引 */
    for (i = 0; i < 16; i++) {
        o[i] = a[i] - b[i];
    }
}

/* 場乘法，含 2^255-19 折返歸約 */
static void field_mul(gf o, const gf a, const gf b)
{
    int64_t t[31];  /* 卷積展開暫存 */
    int i, j;       /* 迴圈索引 */

    for (i = 0; i < 31; i++) {
        t[i] = 0;
    }
    /* schoolbook 乘法卷積 */
    for (i = 0; i < 16; i++) {
        for (j = 0; j < 16; j++) {
            t[i + j] += a[i] * b[j];
        }
    }
    /* 高位以 2^256≡38 折回低位 */
    for (i = 0; i < 15; i++) {
        t[i] += 38 * t[i + 16];
    }
    for (i = 0; i < 16; i++) {
        o[i] = t[i];
    }
    carry25519(o);
    carry25519(o);
}

/* 場平方 */
static void field_sqr(gf o, const gf a)
{
    field_mul(o, a, a);
}

/* 場反元素：以費馬小定理求 a^(p-2) */
static void field_inv(gf o, const gf a)
{
    gf c;    /* 累乘暫存 */
    int i;   /* 指數位元索引 */

    for (i = 0; i < 16; i++) {
        c[i] = a[i];
    }
    /* 由高位往低位逐位元平方，除第 2、4 位外皆乘回底數 */
    for (i = 253; i >= 0; i--) {
        field_sqr(c, c);
        if (i != 2 && i != 4) {
            field_mul(c, c, a);
        }
    }
    for (i = 0; i < 16; i++) {
        o[i] = c[i];
    }
}

/* X25519 純量乘法主體 */
void x25519_scalarmult(uint8_t out[X25519_KEY_LEN],
                       const uint8_t scalar[X25519_KEY_LEN],
                       const uint8_t point[X25519_KEY_LEN])
{
    uint8_t z[32];                 /* 裁切後的私鑰副本 */
    gf x;                          /* 對方公鑰 u 座標 */
    gf a, b, c, d, e, f;           /* Montgomery ladder 工作變數 */
    int i;                         /* 迴圈索引 */
    int64_t r;                     /* 目前處理的私鑰位元 */

    /* 依 RFC 7748 對私鑰做位元裁切（clamp） */
    for (i = 0; i < 31; i++) {
        z[i] = scalar[i];
    }
    z[31] = (uint8_t)((scalar[31] & 127) | 64);
    z[0] &= 248;

    /* 解包對方公鑰 */
    unpack25519(x, point);

    /* 初始化 ladder：a=1, b=x, c=0, d=1 */
    for (i = 0; i < 16; i++) {
        b[i] = x[i];
        a[i] = 0;
        c[i] = 0;
        d[i] = 0;
    }
    a[0] = 1;
    d[0] = 1;

    /* 由高位往低位逐位元進行 Montgomery ladder（255 位元） */
    for (i = 254; i >= 0; i--) {
        r = (z[i >> 3] >> (i & 7)) & 1;   /* 取出目前位元 */
        swap25519(a, b, (int)r);
        swap25519(c, d, (int)r);
        field_add(e, a, c);
        field_sub(a, a, c);
        field_add(c, b, d);
        field_sub(b, b, d);
        field_sqr(d, e);
        field_sqr(f, a);
        field_mul(a, c, a);
        field_mul(c, b, e);
        field_add(e, a, c);
        field_sub(a, a, c);
        field_sqr(b, a);
        field_sub(c, d, f);
        field_mul(a, c, GF_121665);
        field_add(a, a, d);
        field_mul(c, c, a);
        field_mul(a, d, f);
        field_mul(d, b, x);
        field_sqr(b, e);
        swap25519(a, b, (int)r);
        swap25519(c, d, (int)r);
    }

    /* 計算 x 座標 = a / c，並輸出 */
    field_inv(c, c);
    field_mul(a, a, c);
    pack25519(out, a);

    /* 清零私鑰副本 */
    memset(z, 0, sizeof(z));
}

/* 以基點 u=9 產生公鑰 */
void x25519_base(uint8_t out[X25519_KEY_LEN],
                 const uint8_t scalar[X25519_KEY_LEN])
{
    uint8_t basepoint[X25519_KEY_LEN];  /* 基點 u=9 */
    int i;                              /* 迴圈索引 */

    /* 基點為 9，其餘位元組皆為 0 */
    basepoint[0] = 9;
    for (i = 1; i < X25519_KEY_LEN; i++) {
        basepoint[i] = 0;
    }
    x25519_scalarmult(out, scalar, basepoint);
}
