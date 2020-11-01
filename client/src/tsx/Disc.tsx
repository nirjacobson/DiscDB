export interface Track {
    frameOffset: number;
    title: string;
    extendedData?: string;
}

export default interface Disc {
    id: string;
    discId: string;
    artist: string;
    length: number;
    title: string;
    genre: string;
    year: number;
    tracks: ReadonlyArray<Track>;
    extendedData?: string;
    playOrder?: ReadonlyArray<number>;
}
