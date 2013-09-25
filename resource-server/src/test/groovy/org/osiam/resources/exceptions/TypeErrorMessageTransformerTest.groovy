package org.osiam.resources.exceptions

import spock.lang.Specification

class TypeErrorMessageTransformerTest extends Specification {
    def underTest = new TypeErrorMessageTransformer()

    def "should transform message No enum constant org.osiam.storage.entities.PhotoEntity.CanonicalPhotoTypes.huch"(){
        when:
        def result = underTest.transform("No enum constant org.osiam.storage.entities.PhotoEntity.CanonicalPhotoTypes.huch")
        then:
        result == "huch is not a valid Photo type only photo, thumbnail are allowed."
    }

    def "should not transform message No enum constant org.osiam.storage.hahaha.CanonicalPhotoTypes.huch"(){
        when:
        def result = underTest.transform("No enum constant org.osiam.storage.hahaha.CanonicalPhotoTypes.huch")
        then:
        result == "No enum constant org.osiam.storage.hahaha.CanonicalPhotoTypes.huch"
    }

    def "should ignore null"(){
        when:
        def result = underTest.transform(null)
        then:
        !result
    }
}
