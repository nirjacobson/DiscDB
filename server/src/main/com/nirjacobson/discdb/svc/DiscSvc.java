package com.nirjacobson.discdb.svc;

import com.nirjacobson.discdb.dao.DiscDao;
import com.nirjacobson.discdb.model.Disc;
import com.nirjacobson.discdb.svc.exception.DiscErrorCode;
import com.nirjacobson.discdb.svc.exception.SvcException;
import com.nirjacobson.discdb.util.XMCDParser;
import com.nirjacobson.discdb.view.FindResultsView;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.bson.types.ObjectId;

import java.util.Optional;

@ApplicationScoped
public class DiscSvc {
    @Inject
    private DiscDao _discDao;

    public Disc create(final Disc pDisc) throws SvcException {
        final long discId = pDisc.getDiscId();
        final long expectedDiscId = pDisc.calculateDiscId();

        if (discId != expectedDiscId) {
            throw new SvcException(DiscErrorCode.INCORRECT_DISC_ID, discId, expectedDiscId);
        }

        if (pDisc.getId() == null) {
            final Disc disc = new Disc.Builder(pDisc.toDBObject()).id(ObjectId.get()).build();
            _discDao.create(disc);

            return disc;
        } else if (find(pDisc.getId()).isPresent()) {
            throw new SvcException(DiscErrorCode.DUPLICATE_DISC, pDisc.getId());
        } else {
            _discDao.create(pDisc);

            return pDisc;
        }
    }

    public Optional<Disc> find(final ObjectId pId) {
        return _discDao.find(pId);
    }

    public Optional<Disc> find(final Disc pDisc) {
        return _discDao.find(pDisc);
    }

    public FindResultsView find(
            final String pArtist,
            final String pTitle,
            final String pGenre,
            final Integer pYear,
            final Integer pPage) {
        return _discDao.find(pArtist, pTitle, pGenre, pYear, pPage);
    }

    public Disc fromXMCD(final String pXMCD) throws SvcException {
        try {
            return XMCDParser.parse(pXMCD);
        } catch (final XMCDParser.ParseException pE) {
            throw new SvcException(DiscErrorCode.MALFORMED_XMCD);
        }
    }
}
