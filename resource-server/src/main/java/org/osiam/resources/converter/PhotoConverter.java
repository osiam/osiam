/*
 * Copyright (C) 2013 tarent AG
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.osiam.resources.converter;

import java.net.URI;
import java.net.URISyntaxException;

import org.osiam.resources.exceptions.OsiamException;
import org.osiam.resources.scim.Photo;
import org.osiam.storage.entities.PhotoEntity;
import org.springframework.stereotype.Service;

@Service
public class PhotoConverter implements Converter<Photo, PhotoEntity> {

    @Override
    public PhotoEntity fromScim(Photo scim) {
        PhotoEntity photoEntity = new PhotoEntity();
        photoEntity.setValue(scim.getValueAsURI().toString());
        photoEntity.setType(scim.getType());
        photoEntity.setPrimary(scim.isPrimary());
        
        return photoEntity;
    }

    @Override
    public Photo toScim(PhotoEntity entity) {
        Photo scimPhoto;
        
        try {
            scimPhoto =  new Photo.Builder()
                    .setType(entity.getType())
                    .setValue(new URI(entity.getValue()))
                    .setPrimary(entity.isPrimary())
                    .build();
        } catch (URISyntaxException e) {
            throw new OsiamException(e.getMessage());
        }
        return scimPhoto;
    }

}
