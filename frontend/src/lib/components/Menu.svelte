<script lang="ts">
    import {LogoutButton, Navigation, ThemeSwitcher} from "$lib/components";
</script>
<div class="min-w-[260px] bg-primary-foreground rounded-2xl p-5 flex flex-col border">
    <Navigation/>
    <div class="flex items-center justify-between mt-auto">
        <LogoutButton/>
        <ThemeSwitcher/>
    </div>
</div>