#!/bin/sh
# build.sh — 建置 twdiw_tester(Linux / macOS / Git Bash / MSYS2)
# 依序嘗試 $CC、cc、gcc、clang
set -e
cd "$(dirname "$0")"

SRCS="../src/lib/sha256.c ../src/lib/hmac_sha256.c ../src/lib/base64.c \
../src/lib/chacha20_poly1305.c ../src/lib/x25519.c \
../src/lib/totp.c ../src/lib/der_x25519.c ../src/lib/ecc_service.c ../src/lib/qr_parse.c \
../src/twdiw_platform.c ../src/twdiw_verify.c twdiw_tester.c"

OUT=twdiw_tester
EXTRA=""
case "$(uname -s 2>/dev/null)" in
    MINGW*|MSYS*|CYGWIN*) OUT=twdiw_tester.exe; EXTRA="-D__USE_MINGW_ANSI_STDIO=1" ;;
esac

COMPILER=""
for c in "${CC:-}" cc gcc clang; do
    if [ -n "$c" ] && command -v "$c" >/dev/null 2>&1; then
        COMPILER="$c"
        break
    fi
done

if [ -z "$COMPILER" ]; then
    echo "[build] 找不到 C 編譯器(cc / gcc / clang),請先安裝 build-essential 或 Xcode Command Line Tools"
    exit 1
fi

echo "[build] 使用 $COMPILER"
# shellcheck disable=SC2086
"$COMPILER" -std=c99 -O2 -Wall $EXTRA $SRCS -o "$OUT"

echo
echo "[build] 完成 -> $OUT"
if [ ! -f tester_config.txt ]; then
    echo "下一步:cp tester_config.txt.template tester_config.txt,填入金鑰後執行 ./$OUT"
else
    echo "下一步:./$OUT"
fi
