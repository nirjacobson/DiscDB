import path from "path";
import webpack, {Configuration} from "webpack";
import HtmlWebpackPlugin from "html-webpack-plugin";
import ForkTsCheckerWebpackPlugin from "fork-ts-checker-webpack-plugin";

const webpackConfig = (env): Configuration => ({
    entry: "./src/tsx/index.tsx",
    resolve: {
        extensions: [".ts", ".tsx", ".js"],
        modules: [".", "node_modules"]
    },
    output: {
        path: path.join(__dirname, "/dist"),
        filename: "build.js",
        publicPath: "/"
    },
    module: {
        rules: [
            {
                test: /\.tsx?$/,
                loader: "ts-loader",
                options: {
                    transpileOnly: true
                },
                exclude: /dist/
            },
            {
                test: /\.css/,
                use: [{loader: "style-loader"}, {loader: "css-loader"}]
            },
            {
                test: /\.less$/,
                use: [{loader: "style-loader"}, {loader: "css-loader"}, {loader: "less-loader"}]
            }
        ]
    },
    devServer: {
        historyApiFallback: true
    },
    plugins: [
        new HtmlWebpackPlugin({
            template: "./public/index.html"
        }),
        new webpack.DefinePlugin({
            "process.env.PRODUCTION": env.production || !env.development,
            "process.env.NAME": JSON.stringify(require("./package.json").name),
            "process.env.VERSION": JSON.stringify(require("./package.json").version)
        }),
        new ForkTsCheckerWebpackPlugin({
            eslint: {
                files: "./src/**/*.{ts,tsx,js,jsx}" // required - same as command `eslint ./src/**/*.{ts,tsx,js,jsx} --ext .ts,.tsx,.js,.jsx`
            }
        })
    ]
});

export default webpackConfig;
