import {secondsToTimeString} from 'src/tsx/utils/timeUtils';

describe('src/tsx/utils/timeUtils', function () {
    describe('secondsToTimeString()', function () {
        it('returns the proper string for less than 60 seconds: 59 s', function () {
            expect(secondsToTimeString(59)).toBe('59 s');
        });

        it('returns the proper string for 60 seconds: 1:00', function () {
            expect(secondsToTimeString(60)).toBe('1:00');
        });

        it('returns the proper string for 3723 seconds: 1:02:03', function () {
            expect(secondsToTimeString(3723)).toBe('1:02:03');
        });
    });
});
