export interface IColumn {
    id: number;
    name: string;
    items: ICard[]
}
export interface ICard {
    id: number;
    title: string;
    description: string;
}