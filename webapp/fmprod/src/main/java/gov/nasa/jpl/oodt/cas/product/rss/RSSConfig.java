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


package gov.nasa.jpl.oodt.cas.product.rss;

//JDK imports
import java.util.List;
import java.util.Vector;

/**
 * 
 * Configures the {@link RSSProductServlet}, with the information
 * defined in an rssconf.xml file.
 * 
 * @author mattmann
 * @version $Revision$
 * 
 */
public class RSSConfig {

  private List<RSSTag> tags;
  
  private String channelLink;

  /**
   * Default constructor.
   */
  public RSSConfig() {
    this.channelLink = null;
    this.tags = new Vector<RSSTag>();
  }

  /**
   * @return the tags
   */
  public List<RSSTag> getTags() {
    return tags;
  }

  /**
   * @param tags
   *          the tags to set
   */
  public void setTags(List<RSSTag> tags) {
    this.tags = tags;
  }

  /**
   * @return the channelLink
   */
  public String getChannelLink() {
    return channelLink;
  }

  /**
   * @param channelLink the channelLink to set
   */
  public void setChannelLink(String channelLink) {
    this.channelLink = channelLink;
  }

}