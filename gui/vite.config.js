import {defineConfig, transformWithEsbuild} from "vite";
import react from "@vitejs/plugin-react";

const jsAsJsx = {
    name: "js-as-jsx",
    enforce: "pre",
    transform(code, id) {
        return id.includes("/src/") && id.endsWith(".js") ?
            transformWithEsbuild(code, id, {loader: "jsx", jsx: "automatic"}) :
            null;
    }
};

export default defineConfig({
    base: "/budget/",
    plugins: [jsAsJsx, react()],
    build: {
        outDir: "dist/static",
        rollupOptions: {
            output: {
                assetFileNames: ({name}) => name?.endsWith(".css") ? "css/[name]-[hash][extname]" : "assets/[name]-[hash][extname]",
                chunkFileNames: "js/[name]-[hash].js",
                entryFileNames: "js/[name]-[hash].js"
            }
        }
    },
    test: {
        environment: "jsdom",
        globals: true
    }
});
