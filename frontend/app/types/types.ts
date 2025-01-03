import {JwtPayload} from "jsonwebtoken";

interface IUser {
    id: number
    name: string
    email: string
    roles: string[]
    authProvider: string
    enabled: boolean
    accountLocked: boolean
    image: string
}

interface IProduct {
    id: number
    name: string
    image: string
    description: string
    inventory: number
    quantity: number
    price: number
    category: ICategory
    user: IUser
}

interface ICategory {
    id: number
    name: string
    image: string
}

interface IJwt extends JwtPayload {
    id: number
    role: ("BUYER" | "ADMIN" | "SELLER")[]
    username: string
    image: string
    sub: string
    iat: number
    exp: number
}

interface IAddress {
    id: number
    name: string
    name_en: string
}

interface IOrderItemRequest {
    productId: number
    quantity: number
}

interface IPayment {
    amount: number;
    method: string;
    status: EPayment;
}

interface IOrderRequest {
    orderItemList: IOrderItemRequest[];
    paymentInfo: IPayment;
}

interface IOrderItemResponse {
    id: number,
    quantity: number,
    price: number,
    product: IProduct,
}

interface IOrderResponse {
    id: number,
    total: number,
    status: string,
    orderItemList: IOrderItemResponse[]
    buyer: IUser,
    seller: IUser,
    createdAt: Date,
}

enum EPayment {
    PENDING,
    CONFIRMED,
    SHIPPED,
    DELIVERED,
    CANCELLED,
    RETURNED,
}

interface IProductInCart {
    id: number
    name: string
    image: string
    quantity: number
    price: number
    checkbox: boolean
}

export {EPayment}
export type {IProduct, IUser, ICategory, IJwt, IAddress, IOrderItemRequest,
    IOrderRequest, IOrderResponse, IOrderItemResponse, IProductInCart}