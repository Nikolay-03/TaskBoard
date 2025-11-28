import type {IUser} from "$api/user";

export interface IRegisterBody {
    email: string
    password: string
    name: string
}
export interface ILoginBody {
    email: string
    password: string
}
export interface ILoginResult {
    user: IUser
    sessionId: string
}