export interface IUser {
    id: number;
    email: string;
    name: string;
    createdAt: string;
    avatar: string | null;
}

export type IUpdateUserBody = Partial<Pick<IUser, 'name' | 'avatar'>>