const VUE_APP_RSA_PUBLIC_KEY = process.env.VITE_RSA_PUBLIC_KEY || "";

export const RSAencrypt = async (pwd) => {
  if (!VUE_APP_RSA_PUBLIC_KEY) {
    throw new Error("VITE_RSA_PUBLIC_KEY is missing");
  }

  const pemKeyPem = `-----BEGIN PUBLIC KEY-----\n${VUE_APP_RSA_PUBLIC_KEY}\n-----END PUBLIC KEY-----`;
  const pemHeader = "-----BEGIN PUBLIC KEY-----";
  const pemFooter = "-----END PUBLIC KEY-----";
  const pemContents = pemKeyPem
    .replace(pemHeader, "")
    .replace(pemFooter, "")
    .replace(/\s/g, "");

  // 將 Base64 轉成二進制
  const binaryDer = Uint8Array.from(atob(pemContents), (c) => c.charCodeAt(0));

  // 將 PEM 格式的公鑰轉換成可用的 CryptoKey
  const publicKey = await window.crypto.subtle.importKey(
    "spki", // 公鑰的格式
    binaryDer,
    {
      name: "RSA-OAEP",
      hash: "SHA-256"
    },
    true,
    ["encrypt"]
  );

  // 將資料轉為 ArrayBuffer
  const encoder = new TextEncoder();
  const encodedData = encoder.encode(pwd);

  // 使用 RSA-OAEP 加密資料
  const encrypted = await window.crypto.subtle.encrypt(
    {
      name: "RSA-OAEP",
      hash: "SHA-256"
    },
    publicKey,
    encodedData
  );

  const encrypedBase64 = btoa(
    String.fromCharCode(...new Uint8Array(encrypted))
  );

  return encrypedBase64;
};
