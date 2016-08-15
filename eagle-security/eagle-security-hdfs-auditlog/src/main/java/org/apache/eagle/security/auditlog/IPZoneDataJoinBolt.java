/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.eagle.security.auditlog;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import com.typesafe.config.Config;
import org.apache.eagle.security.auditlog.timer.IPZonePollingJob;
import org.apache.eagle.security.entity.IPZoneEntity;
import org.apache.eagle.security.util.ExternalDataCache;
import org.apache.eagle.security.util.ExternalDataJoiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

public class IPZoneDataJoinBolt extends BaseRichBolt {
	private static final Logger LOG = LoggerFactory.getLogger(IPZoneDataJoinBolt.class);
	private Config config;
	private OutputCollector collector;

	public IPZoneDataJoinBolt(Config config){
		this.config = config;
	}

	@Override
	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
		this.collector = collector;
		// start ipzone data polling
		try{
			ExternalDataJoiner joiner = new ExternalDataJoiner(IPZonePollingJob.class, config, context.getThisComponentId() + "." + context.getThisTaskIndex());
			joiner.start();
		}catch(Exception ex){
			LOG.error("Fail bring up quartz scheduler", ex);
			throw new IllegalStateException(ex);
		}
	}

	@Override
	public void execute(Tuple input) {
		try {
			Map<String, Object> toBeCopied = (Map<String, Object>) input.getValue(1);
			Map<String, Object> event = new TreeMap<String, Object>(toBeCopied); // shallow copy
			Map<String, IPZoneEntity> map = (Map<String, IPZoneEntity>) ExternalDataCache.getInstance().getJobResult(IPZonePollingJob.class);
			IPZoneEntity e = null;
			if (map != null) {
				e = map.get(event.get("host"));
			}
			event.put("securityZone", e == null ? "NA" : e.getSecurityZone());
			if (LOG.isDebugEnabled()) LOG.debug("After IP zone lookup: " + event);
			collector.emit(Arrays.asList(event.get("user"), event));
		}catch(Exception ex){
			LOG.error("error joining data, ignore it", ex);
		}finally {
			collector.ack(input);
		}
    }

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("user", "message"));
	}
}
