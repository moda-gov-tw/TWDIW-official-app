# 使用官方的 Nginx 基礎映像檔
FROM nginxinc/nginx-unprivileged:alpine

# 複製靜態 HTML 檔案到 Nginx 預設的網頁目錄
COPY ./spa /usr/share/nginx/html/
COPY ./default.conf /etc/nginx/conf.d
COPY ./nginx.conf /etc/nginx
COPY ./403.html /usr/share/nginx/html/

USER root

# 確保 nginx 用戶擁有 /var/log/nginx 的寫入權限
RUN chown -R nginx:nginx /var/log/nginx

USER nginx

# 清除舊的 log 檔案（不需要 root 權限）
RUN rm -rf /var/log/nginx/*.log
