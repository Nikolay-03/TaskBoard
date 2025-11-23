// @ts-nocheck


export const routes = {
  "meta": {},
  "id": "_default",
  "name": "",
  "file": {
    "path": "src/routes",
    "dir": "src",
    "base": "routes",
    "ext": "",
    "name": "routes"
  },
  "rootName": "default",
  "routifyDir": import.meta.url,
  "children": [
    {
      "meta": {
        "dynamic": true,
        "order": false,
        "dynamicSpread": true
      },
      "id": "_default_____notfound__svelte",
      "name": "[...notfound]",
      "file": {
        "path": "src/routes/[...notfound].svelte",
        "dir": "src/routes",
        "base": "[...notfound].svelte",
        "ext": ".svelte",
        "name": "[...notfound]"
      },
      "asyncModule": () => import('../src/routes/[...notfound].svelte'),
      "children": []
    },
    {
      "meta": {
        "isDefault": true
      },
      "id": "_default_index_svelte",
      "name": "index",
      "file": {
        "path": "src/routes/index.svelte",
        "dir": "src/routes",
        "base": "index.svelte",
        "ext": ".svelte",
        "name": "index"
      },
      "asyncModule": () => import('../src/routes/index.svelte'),
      "children": []
    },
    {
      "meta": {},
      "id": "_default_layout_svelte",
      "name": "layout",
      "file": {
        "path": "src/routes/layout.svelte",
        "dir": "src/routes",
        "base": "layout.svelte",
        "ext": ".svelte",
        "name": "layout"
      },
      "asyncModule": () => import('../src/routes/layout.svelte'),
      "children": []
    }
  ]
}
export default routes