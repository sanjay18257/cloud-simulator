export default defineConfig({
  plugins: [react()],
  server: {
    middleware: (app) => {
      app.use((req, res, next) => {
        if (req.url.endsWith('.jar')) {
          res.setHeader('Content-Type', 'application/java-archive');
        }
        if (req.url.endsWith('.zip')) {
          res.setHeader('Content-Type', 'application/zip');
        }
        next();
      });
    }
  }
});
