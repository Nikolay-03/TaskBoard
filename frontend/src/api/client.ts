import { createClient } from "@hyper-fetch/core";

export const client = createClient({
    url: "https://jsonplaceholder.typicode.com",
});