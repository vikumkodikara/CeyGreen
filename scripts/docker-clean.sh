#!/usr/bin/env bash
# CeyGreen Docker Storage Cleaner (Linux / AWS EC2)
# Safely frees disk space from old images, build cache, and dead containers.

set -e

echo "========================================="
echo "   CeyGreen Docker Storage Cleaner (EC2) "
echo "========================================="

echo -e "\n--- Disk usage BEFORE cleanup ---"
df -h /

echo -e "\n--- [1/3] Pruning stopped containers & unused networks ---"
docker container prune -f || true
docker network prune -f || true

echo -e "\n--- [2/3] Pruning unused images & dangling layers ---"
docker image prune -af || true

echo -e "\n--- [3/3] Pruning BuildKit build cache ---"
docker builder prune -af || true

echo -e "\n--- Docker Storage Breakdown ---"
docker system df || true

echo -e "\n--- Disk usage AFTER cleanup ---"
df -h /
echo "========================================="
