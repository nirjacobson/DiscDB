import Disc from "src/tsx/Disc";

interface FindTerms {
    artist?: string;
    title?: string;
    genre?: string;
    year?: number;
}

interface FindResults {
    results: Array<Disc>;
    pages: number;
}

export default {
    find: (terms: FindTerms, page: number): Promise<FindResults> => {
        const params = Object.entries({
            ...terms,
            page
        })
            .filter((entry) => !!entry[1])
            .map((entry) => `${entry[0]}=${encodeURIComponent(entry[1] as string)}`)
            .join("&");

        return fetch(`/api/v1.0/find?${params}`).then((response) => response.json());
    }
};
