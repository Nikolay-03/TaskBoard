import type {BadgeVariant} from "$lib/ui/badge";

export interface IUserBadgeItem {
    id: number;
    name: string;
    color?: BadgeVariant;
    avatar?: string | null
    onDelete?: (id: number) => void;
}