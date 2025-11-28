import type { IColumn } from '$api/column';

export interface IBoard {
	id: number;
	ownerId: number;
	title: string;
	description: string;
	createdAt: string;
	updatedAt: string;
	columns: IColumn[];
}
