package ubb.scs.map.repository.database;

import ubb.scs.map.domain.Prietenie;
import ubb.scs.map.dto.PrietenieFilterDto;
import ubb.scs.map.repository.PagingRepository;
import ubb.scs.map.utils.paging.Page;
import ubb.scs.map.utils.paging.Pageable;

public interface PrietenieRepository extends PagingRepository<Long, Prietenie> {
    Page<Prietenie> findAllOnPage(Pageable pageable, PrietenieFilterDto prietenieFilter);
}
