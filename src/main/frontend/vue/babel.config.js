module.exports = {
  "presets": [
    [
      "@vue/cli-plugin-babel/preset",
      {
        "useBuiltIns": "entry"
      }
    ]
  ],
  "plugins": ["@babel/plugin-proposal-optional-chaining","@babel/plugin-proposal-nullish-coalescing-operator"]
}