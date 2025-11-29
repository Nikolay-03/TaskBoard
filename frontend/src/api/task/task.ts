import type { IAssignee } from '$api/assignees';
import type { ILabel } from '$api/labels';
import type { IParticipant } from '$api/participants';

export interface ITask {
	id: number;
	boardId: number;
	columnId: number;
	title: string;
	description: string;
	position: number;
	dueDate: string;
	createdAt: string;
	updatedAt: string;
	assignees: IAssignee[];
	participants: IParticipant[];
	labels: ILabel[];
}

interface ICreateTaskBody {
	title: string;
	description: string;
	dueDate: string;
	labels: number[];
	participants: number[];
	assignees: number[];
}
interface IUpdateTaskBody extends ICreateTaskBody {
	columnId: number;
	position: number;
}
export interface ICreateTaskVariables {
	columnId: number;
	body: ICreateTaskBody;
}
export interface IUpdateTaskVariables {
	id: number;
	body: Partial<IUpdateTaskBody>;
}
