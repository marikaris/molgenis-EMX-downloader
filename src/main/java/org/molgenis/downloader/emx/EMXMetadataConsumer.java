
package org.molgenis.downloader.emx;

import org.molgenis.downloader.api.EMXWriter;
import org.molgenis.downloader.api.metadata.Metadata;
import org.molgenis.downloader.emx.serializers.EMXAttributeSerializer;
import org.molgenis.downloader.emx.serializers.EMXTagSerializer;
import org.molgenis.downloader.emx.serializers.EMXPackageSerializer;
import org.molgenis.downloader.emx.serializers.EMXEntitySerializer;
import java.io.IOException;
import java.util.Collection;
import org.molgenis.downloader.api.EntitySerializer;
import org.molgenis.downloader.api.MetadataConsumer;
import org.molgenis.downloader.api.MetadataRepository;
import org.molgenis.downloader.api.metadata.MolgenisVersion;
import org.molgenis.downloader.api.EMXDataStore;


class EMXMetadataConsumer implements MetadataConsumer {

    private static final String ATTRIBUTES = "attributes";
    private static final String ENTITIES = "entities";
    private static final String PACKAGES = "packages";
    private static final String TAGS = "tags";
    private final MolgenisVersion version;
    private final EMXWriter writer;

    public EMXMetadataConsumer(EMXWriter writer, MolgenisVersion molgenisVersion) {
        version = molgenisVersion;
        this.writer = writer;
    }

    @Override
    public void accept(final MetadataRepository repository) {
        try {
            EMXAttributeSerializer attributesSerializer = new EMXAttributeSerializer(version, repository.getLanguages());
            writeMetadata(ATTRIBUTES, attributesSerializer, repository.getAttributes());

            EMXEntitySerializer entitiesSerializer = new EMXEntitySerializer(repository.getLanguages());
            writeMetadata(ENTITIES, entitiesSerializer, repository.getEntities());

            EMXPackageSerializer packagesSerializer = new EMXPackageSerializer();
            writeMetadata(PACKAGES, packagesSerializer, repository.getPackages());

            EMXTagSerializer tagSerializer = new EMXTagSerializer();
            writeMetadata(TAGS, tagSerializer, repository.getTags());
        } catch (final IOException ex) {
            writer.addException(ex);
        }
    }
    
    private <M extends Metadata> void writeMetadata(final String name, EntitySerializer serializer, final Collection<M> metadata) throws IOException {
        EMXDataStore sheet = writer.createDataStore(name);
        sheet.writeRow(serializer.fields());
        for (final M m : metadata) {
            sheet.writeRow(serializer.serialize(m));
        }
    }
}
