const http = require('http');

const LISTEN_HOST = '192.168.137.1';
const LISTEN_PORT = 8085;
const TARGET_HOST = '127.0.0.1';
const TARGET_PORT = 8082;

http
  .createServer((req, res) => {
    const path = (req.url || '/').replace(/^\/api/, '') || '/';
    const headers = { ...req.headers, host: `${TARGET_HOST}:${TARGET_PORT}` };
    const proxyReq = http.request(
      { hostname: TARGET_HOST, port: TARGET_PORT, path, method: req.method, headers },
      (proxyRes) => {
        res.writeHead(proxyRes.statusCode || 502, proxyRes.headers);
        proxyRes.pipe(res);
      }
    );
    proxyReq.on('error', (err) => {
      res.writeHead(502, { 'Content-Type': 'text/plain' });
      res.end(err.message);
    });
    req.pipe(proxyReq);
  })
  .listen(LISTEN_PORT, LISTEN_HOST, () => {
    console.log(`proxy ${LISTEN_HOST}:${LISTEN_PORT}/api/iot -> ${TARGET_HOST}:${TARGET_PORT}/iot`);
  });
