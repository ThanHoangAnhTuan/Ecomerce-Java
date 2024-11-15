import {cookies} from 'next/headers'
import HeaderClient from "@/app/components/HeaderClient";

const HeaderServer = async () => {
    const cookieStore = await cookies()
    const token = cookieStore.get('jwt')
    return (
        <HeaderClient token={token?.value ?? ""}></HeaderClient>
    )
}

export default HeaderServer