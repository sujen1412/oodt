/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.oodt.cas.curation.directory;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import java.util.ArrayList;
import java.util.List;
import org.apache.oodt.commons.validation.DirectoryValidator;

public class S3Directory implements Directory {

  String directory = null;
  private  AWSCredentials credentials = null;
  private DirectoryValidator validator;
  private  String bucketName = null;
  /**
   * Build the object around a set directory
   * @param directory
   */
  public S3Directory(String directory, DirectoryValidator validator,
      String bucketName) {
    this.directory = directory;
    this.validator = validator;
    this.bucketName = bucketName;
    this.loadCredentials();
  }

  private void loadCredentials() {
    try {
      credentials = new ProfileCredentialsProvider().getCredentials();
    } catch (Exception e) {
      System.out.print(e);
    }
  }

  private List<String> listObjects() {
    AmazonS3 s3 = new AmazonS3Client(credentials);
    List<String> files = new ArrayList<String>();
    ObjectListing listing = s3.listObjects(new ListObjectsRequest()
        .withBucketName(bucketName));
    for (S3ObjectSummary objectSummary : listing.getObjectSummaries()) {
      files.add(objectSummary.getKey());
    }
    return files;
  }

  @Override
  public DirectoryListing list() throws Exception {
    return S3DirectoryListing.lisingFromFileObjects(this.listObjects(), validator);
  }

}