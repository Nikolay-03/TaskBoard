import type {ITask} from "$api/task";

export interface IColumn {
    id: number
    boardId: number
    title: string
    position: number
    tasks: ITask[]
}