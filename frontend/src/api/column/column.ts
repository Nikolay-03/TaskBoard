import type { ITask } from '$api/task';

export interface IColumn {
	id: number;
	boardId: number;
	title: string;
	position: number;
	tasks: ITask[];
}
export interface ICreateColumnBody {
	title: string;
}
interface IUpdateColumnBody {
	title: string;
	position: number;
}
export interface IUpdateColumnVariables {
	id: number;
	body: Partial<IUpdateColumnBody>;
}
