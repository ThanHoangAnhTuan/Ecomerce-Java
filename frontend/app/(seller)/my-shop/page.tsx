import ClientMyShop from "@/app/(seller)/my-shop/ClientMyShop";
import {cookies} from "next/headers";
import jwt, {JwtPayload} from "jsonwebtoken"
import {IJwt} from "@/app/types/types";
import {redirect, RedirectType} from "next/navigation";
import HeaderServer from "@/app/components/HeaderServer";

const MyShop = async () => {
    const cookieStore = await cookies()
    const token = cookieStore.get('jwt')
    if (!token) {
        redirect("/", RedirectType.replace)
    }
    const decoded: JwtPayload | string | null = jwt.decode(token?.value ?? "")
    if (!decoded) {
        redirect("/", RedirectType.replace)
    }
    const jwtPayload: IJwt = decoded as IJwt;
    if (!jwtPayload.role.includes("SELLER")) {
        redirect("/", RedirectType.replace)
    }
    return (
        <>
            <HeaderServer></HeaderServer>
            <ClientMyShop jwt={jwtPayload}/>
        </>
    )
}

export default MyShop