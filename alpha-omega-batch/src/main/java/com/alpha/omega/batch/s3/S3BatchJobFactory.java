package com.alpha.omega.batch.s3;

import com.alpha.omega.batch.BatchJobFactory;
import com.alpha.omega.batch.BatchRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;
import org.springframework.batch.item.support.SynchronizedItemStreamReader;
import org.springframework.core.io.Resource;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.alpha.omega.batch.BatchConstants.RESOURCE_FILTER_KEY;


public interface S3BatchJobFactory<R,T> extends BatchJobFactory<T> {

    ResourceAwareItemReaderItemStream<? extends R> getDelegate(BatchRequest<T> batchApplicantRequest);
    List<Resource> getS3Resources(BatchRequest<T> batchApplicantRequest);

    public default SynchronizedItemStreamReader<R> createS3DataReader(BatchRequest<T> batchApplicantRequest) {
        SynchronizedItemStreamReader synchronizedItemStreamReader = new SynchronizedItemStreamReader();

        List<Resource> resourceList = getS3Resources(batchApplicantRequest);
        Resource[] resources = resourceList.toArray(new Resource[resourceList.size()]);
        MultiResourceItemReader<R> multiResourceItemReader = new MultiResourceItemReader<>();
        multiResourceItemReader.setName(batchApplicantRequest.getMultiReaderName());
        multiResourceItemReader.setResources(resources);
        multiResourceItemReader.setComparator(DESCRIPTION_COMPARATOR);
        multiResourceItemReader.setDelegate(getDelegate(batchApplicantRequest));
        synchronizedItemStreamReader.setDelegate(multiResourceItemReader);
        return synchronizedItemStreamReader;
    }

    Comparator<Resource> DESCRIPTION_COMPARATOR = new Comparator<>() {

        /**
         * Compares resource description
         */
        @Override
        public int compare(Resource r1, Resource r2) {
            return r1.getDescription().compareTo(r2.getDescription());
        }

    };

    default Predicate<Resource> findResourcePredicate(BatchRequest  batchRequest){
        return resource -> findResourceFunction(batchRequest).apply(resource);
    }

    default Function<Resource, Boolean> findResourceFunction(BatchRequest  batchRequest){
        return resource -> {
            String resourceFilter = (String)batchRequest.getJobParameters().get(RESOURCE_FILTER_KEY);
            if (StringUtils.isNotBlank(resourceFilter)){
                return resource.getDescription().contains(resourceFilter);
            } else {
                return Boolean.TRUE;
            }
        };
    }

}
