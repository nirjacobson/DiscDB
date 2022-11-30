import Disc from 'src/tsx/Disc';

const FRAMES_PER_SECOND = 75;

const framesToSeconds = (frames: number): number => {
    return frames / FRAMES_PER_SECOND;
};

const trackLength = (disc: Disc, trackIndex: number): number => {
    if (trackIndex === disc.tracks.length - 1) {
        return Math.ceil(
            disc.length -
                framesToSeconds(disc.tracks[trackIndex].frameOffset - disc.tracks[0].frameOffset)
        );
    } else {
        return Math.ceil(
            framesToSeconds(
                disc.tracks[trackIndex + 1].frameOffset - disc.tracks[trackIndex].frameOffset
            )
        );
    }
};

const secondsToTimeString = (seconds: number): string => {
    const h = Math.floor(seconds / 3600);
    const m = Math.floor((seconds - h * 3600) / 60);
    const s = seconds - h * 3600 - m * 60;

    if (h > 0) {
        return `${h}:${m < 10 ? 0 : ''}${m}:${s < 10 ? 0 : ''}${s}`;
    } else if (m > 0) {
        return `${m}:${s < 10 ? 0 : ''}${s}`;
    } else {
        return `${s} s`;
    }
};

export {framesToSeconds, trackLength, secondsToTimeString};
