package cl.ucn.disc.dam.pictwinultimate.domain;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import cl.ucn.disc.dam.pictwinultimate.Database;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Clase que relaciona 2 {@link Pic}.
 */
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(
        database = Database.class,
        cachingEnabled = false,
        orderedCursorLookUp = true,
        cacheSize = Database.CACHE_SIZE
)
public class Twin extends BaseModel {

    /**
     * Pic local
     */
    @Getter
    @Setter
    @PrimaryKey
    @ForeignKey(tableClass = Pic.class)
    @Column
    Pic local;

    /**
     * Pic desde el servidor
     */
    @Column
    @Getter
    @Setter
    @PrimaryKey
    @ForeignKey(tableClass = Pic.class)
    Pic remote;

}
