import Menu from "@/app/components/Menu";
import ProductList from "@/app/components/ProductList";
import HeaderServer from "@/app/components/HeaderServer";

export default function Home() {
    return (
        <>
            <HeaderServer></HeaderServer>
            <main className={"bg-[#f1f1f1] flex px-20 py-5 h-screen"}>
                <Menu></Menu>
                <ProductList></ProductList>
            </main>
        </>
    )
}
