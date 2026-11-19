#!/usr/bin/env bash
# Circular Price Finder - one-shot installer for Ubuntu/Debian VPS (run as root)
# Usage: bash install.sh            -> serves on http://<server-ip>/
#        bash install.sh example.com -> serves on that domain (+ tries free HTTPS)
set -e
DOMAIN="${1:-_}"
DIR=/var/www/circular-price-finder
HERE="$(cd "$(dirname "$0")" && pwd)"
apt-get update -y && apt-get install -y nginx
mkdir -p "$DIR" && cp -r "$HERE/site/." "$DIR/"
cat > /etc/nginx/sites-available/circular-price-finder <<NG
server {
    listen 80;
    server_name $DOMAIN;
    root $DIR;
    index index.html;
    location / { try_files \$uri \$uri/ =404; }
    location = /data.json { add_header Cache-Control "no-cache"; }
    gzip on; gzip_types application/json text/css application/javascript;
}
NG
ln -sf /etc/nginx/sites-available/circular-price-finder /etc/nginx/sites-enabled/circular-price-finder
[ "$DOMAIN" = "_" ] && rm -f /etc/nginx/sites-enabled/default
nginx -t && systemctl reload nginx || systemctl restart nginx
command -v ufw >/dev/null && ufw allow 'Nginx Full' >/dev/null 2>&1 || true
if [ "$DOMAIN" != "_" ]; then
  apt-get install -y certbot python3-certbot-nginx && certbot --nginx -d "$DOMAIN" --non-interactive --agree-tos --register-unsafely-without-email --redirect || echo "HTTPS skipped: point the domain's A record to this server, then run: certbot --nginx -d $DOMAIN"
fi
echo "Done. Open: http://${DOMAIN/_/$(curl -s -4 ifconfig.me || hostname -I | awk '{print $1}')}/"
