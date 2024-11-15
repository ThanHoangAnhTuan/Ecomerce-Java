import HeaderServer from "@/app/components/HeaderServer";
import SearchPageClient from "@/app/search/SearchPageClient";
import {Suspense} from "react";

const SearchPage = () => {
    return (
        <>
            <HeaderServer></HeaderServer>
            <Suspense>
                <SearchPageClient></SearchPageClient>
            </Suspense>
        </>
    )
}

export default SearchPage