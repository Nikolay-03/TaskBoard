import type { IColumn } from '$api/column';

export interface IBoard {
	id: number;
	ownerId: number;
	title: string;
	description: string;
	createdAt: string;
	updatedAt: string;
	isFavorite: boolean;
}
export interface IBoardView extends IBoard {
	columns: IColumn[];
}

export interface ICreateBoardBody {
	title: string;
	description?: string;
	members?: number[];
}
interface IUpdateBoardBody {
	title: string;
	description?: string;
	members?: number[];
}
export interface IUpdateBoardVariables {
	id: number;
	body: Partial<IUpdateBoardBody>;
}
export interface OptimisticContext {
	previousBoards?: IBoard[];
	previousBoard?: IBoardView;
	previousFavorites?: IBoard[];
}
