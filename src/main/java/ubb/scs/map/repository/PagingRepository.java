package ubb.scs.map.repository;

import ubb.scs.map.domain.Entity;
import ubb.scs.map.utils.paging.Page;
import ubb.scs.map.utils.paging.Pageable;

public interface PagingRepository<ID , E extends Entity<ID>> extends Repository<ID, E> {
    Page<E> findAllOnPage(Pageable pageable);
}
