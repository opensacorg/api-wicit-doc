package org.opensac.wicitapi.vendor;

import org.junit.jupiter.api.Test;
import org.opensac.wicitapi.common.PageRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Unit tests that construct the service directly with a fake repository.
 */
class VendorServiceTest {

    private final VendorRepository repository = mock(VendorRepository.class);
    private final VendorService service = new VendorService(repository);

    @Test
    void findInBoundsPassesTheBoxToTheRepository() {
        var vendor = new VendorResponse(
                1L, "SAVE MART #609", "11980 HIGHWAY 88", null, "Jackson", "95642", "Amador", 38.36, -120.80);
        when(repository.findInBounds(38.0, 39.0, -121.0, -120.0, 10)).thenReturn(List.of(vendor));

        var vendors = service.findInBounds(new VendorService.Bounds(-121.0, 38.0, -120.0, 39.0), 10);

        assertThat(vendors).containsExactly(vendor);
    }

    @Test
    void findInBoundsCapsTheLimit() {
        service.findInBounds(new VendorService.Bounds(-180, -90, 180, 90), 50_000);

        verify(repository).findInBounds(-90, 90, -180, 180, VendorService.MAX_IN_BOUNDS);
    }

    @Test
    void listWorksOutThePageCount() {
        when(repository.find("Sacramento", null, new PageRequest(0, 50))).thenReturn(List.of());
        when(repository.count("Sacramento", null)).thenReturn(139);

        var page = service.list("Sacramento", null, 0, 50);

        assertThat(page.totalItems()).isEqualTo(139);
        assertThat(page.totalPages()).isEqualTo(3);
    }
}
