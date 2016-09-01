/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.getusroi.mesh.camel;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.ScheduledPollEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.HazelcastInstance;

/**
 * Represents a HelloWorld endpoint.
 */
public class MeshHCEventEndpoint extends ScheduledPollEndpoint {
	final Logger logger = LoggerFactory.getLogger(MeshHCEventEndpoint.class); 
	 private String eventId;
	 private String subscriberId;
	 private HazelcastInstance hcInstance;
   
    public MeshHCEventEndpoint(String uri, MeshComponent component,HazelcastInstance hcInstance) {
        super(uri, component);
        this.hcInstance=hcInstance;
    }

    public Producer createProducer() throws Exception {
       logger.debug(">>.createProducer()"); 
   	 return new MeshEventProducer(this);
    }

    public Consumer createConsumer(Processor processor) throws Exception {
   	 logger.debug(">>.createConsumer() processor"+processor+" hcInstance="+hcInstance); 
   	 Consumer consumer=new MeshHCEventConsumer(this, processor,hcInstance);
   	 configureConsumer(consumer);
   	 return consumer;
    }

    public boolean isSingleton() {
        return true;
    }

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public String getSubscriberId() {
		return subscriberId;
	}

	public void setSubscriberId(String subscriberId) {
		this.subscriberId = subscriberId;
	}

	public HazelcastInstance getHcInstance() {
		return hcInstance;
	}

	    
}