import React from 'react';
import styled from '@emotion/styled';

const Page = styled.div``;

const Endpoint = styled.div`
    margin-bottom: 16px;
`;

const Title = styled.h1`
    font-family: Monospace;
`;
const Description = styled.div``;

const API = (): JSX.Element => (
    <Page>
        The API returns CD metadata as Disc objects whose schema is given below:
        <pre>
            {`
Disc {
  id: string;                   // DiscDB ID (hex string)
  discId: string;               // XMCD disc ID (hex string)
  artist: string;
  length: number;               // Disc length in seconds
  title: string;
  genre: string;
  year: number;
  tracks: Array<Track>;
  extendedData?: string;        // Extra disc information
  playOrder?: Array<number>;    // An array of track indices: the order in which the tracks are to be played
}

Track {
  frameOffset: number;          // Offset of the track on the disc where a frame is 1/75th of a second
  title: string;                // Track title
  extendedData?: string;        // Extra track information
}`}
        </pre>
        <Endpoint>
            <Title>GET /api/v1.0/{'{id}'}</Title>
            <Description>Get a disc by its DiscDB ID.</Description>
        </Endpoint>
        <Endpoint>
            <Title>
                GET /api/v1.0/find?artist={'{a}'}&title={'{t}'}&genre={'{g}'}&year=
                {'{y}'}&page={'{p}'}
            </Title>
            <Description>
                Find discs by any combination of artist, title, genre and year. Case-insensitive
                matches are performed using the terms given. This endpoint returns:
                <pre>
                    {`
Results {
  results: Array<Disc>;         // The requested page of up to 10 results
  pages: number;                // The number of pages of results
}`}
                </pre>
            </Description>
        </Endpoint>
        <Endpoint>
            <Title>POST /api/v1.0/find</Title>
            <Description>
                Find a disc using its structural properties which can be read from the disc itself.
                The POST body is a Disc with this reduced schema:
                <pre>
                    {`
Disc {
  discId: string;
  length: number;
  tracks: Array<Track>;
}

Track {
  frameOffset: number;
}`}
                </pre>
                The XMCD disc ID verifies the structure of the disc and is calculated with the
                following algorithm:
                <pre>
                    {`
uint32 calculateDiscId() {
  if (tracks.length == 0) return 0;

  uint32 result = 0;
  uint32 temp;

  for (Track track : tracks) {
    temp = framesToSeconds(track.frameOffset);
    do {
      result += temp % 10;
      temp /= 10;
    } while (temp != 0);
  }

  uint32 discID =
      ((result % 0xff) << 24)
          | ((length - framesToSeconds(tracks[0].frameOffset)) << 8)
          | tracks.length;

  return discID;
}`}
                </pre>
            </Description>
        </Endpoint>
        <Endpoint>
            <Title>POST /api/v1.0</Title>
            <Description>
                Add a Disc to the database. The POST body is a Disc whose DiscDB ID is omitted. The
                response is the Disc created.
            </Description>
        </Endpoint>
        <Endpoint>
            <Title>POST /api/v1.0/xmcd</Title>
            <Description>
                Add a Disc to the database. The POST body is the text of an XMCD file. The response
                is the Disc created.
            </Description>
        </Endpoint>
    </Page>
);

export default API;
