<script context="module">
    import {QueryClientProvider} from '@tanstack/svelte-query'
    import {Router} from 'sv-router';
    import {queryClient} from "$api";
    import 'sv-router/generated';

</script>
<QueryClientProvider client={queryClient}>
    <Router/>
</QueryClientProvider>