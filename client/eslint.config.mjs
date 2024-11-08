import typescriptEslint from "@typescript-eslint/eslint-plugin";
import react from "eslint-plugin-react";
import reactHooks from "eslint-plugin-react-hooks";
import _import from "eslint-plugin-import";
import prettier from "eslint-plugin-prettier";
import { fixupPluginRules } from "@eslint/compat";
import globals from "globals";
import tsParser from "@typescript-eslint/parser";
import path from "node:path";
import { fileURLToPath } from "node:url";
import js from "@eslint/js";
import { FlatCompat } from "@eslint/eslintrc";

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
const compat = new FlatCompat({
    baseDirectory: __dirname,
    recommendedConfig: js.configs.recommended,
    allConfig: js.configs.all
});

export default [{
    ignores: ["**/webpack.config.ts"],
}, ...compat.extends(
    "plugin:@typescript-eslint/recommended",
    "plugin:react/recommended",
    "plugin:prettier/recommended",
), {
    plugins: {
        "@typescript-eslint": typescriptEslint,
        react,
        "react-hooks": fixupPluginRules(reactHooks),
        import: fixupPluginRules(_import),
        prettier,
    },

    languageOptions: {
        globals: {
            ...globals.browser,
        },

        parser: tsParser,
        ecmaVersion: 2020,
        sourceType: "module",

        parserOptions: {
            project: ["tsconfig.json"],

            ecmaFeatures: {
                jsx: true,
            },
        },
    },

    settings: {
        react: {
            version: "detect",
        },
    },

    rules: {
        "@typescript-eslint/explicit-function-return-type": "off",
        "@typescript-eslint/no-unused-vars": "off",

        "prettier/prettier": ["error", {
            singleQuote: true,
        }],

        "react/jsx-filename-extension": ["warn", {
            extensions: [".jsx", ".tsx"],
        }],

        "react/prop-types": "off",
        "react-hooks/rules-of-hooks": "error",
        "react-hooks/exhaustive-deps": "warn",

        "react/no-unknown-property": ["error", {
            ignore: ["css"],
        }],
    },
}];