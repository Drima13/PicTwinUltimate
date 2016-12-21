package cl.ucn.disc.dam.pictwinultimate.domain;

import com.raizlabs.android.dbflow.annotation.Column;
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
 * Pic
 *
 */
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(
        database = Database.class,
        cachingEnabled = true,
        orderedCursorLookUp = true,
        cacheSize = Database.CACHE_SIZE
)
public class Pic extends BaseModel {

    /**
     * Iden tificador unico
     */
    @Getter
    @Column
    @PrimaryKey(autoincrement = true)
    Long id;

    /**
     * Iden tificador unico
     */
    @Getter
    @Column
    @Setter
    Long dbId;

    /**
     * Identificador del dispositivo
     */
    @Getter
    @Column
    String deviceId;

    /**
     * Fecha de la foto
     */
    @Getter
    @Column
    Long date;

    /**
     * URL de la foto
     */
    @Getter
    @Column
    String url;

    /**
     * Latitud
     */
    @Getter
    @Column
    Double latitude;

    /**
     * Longitud
     */
    @Getter
    @Column
    Double longitude;

    /**
     * Numero de likes
     */
    @Getter
    @Column
    Integer positive;

    /**
     * Numero de dis-likes
     */
    @Getter
    @Column
    Integer negative;

    /**
     * Numero de warnings
     */
    @Column
    @Getter
    Integer warning;

}
