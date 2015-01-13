/*!
 * PENTAHO CORPORATION PROPRIETARY AND CONFIDENTIAL
 *
 * Copyright 2002 - 2014 Pentaho Corporation (Pentaho). All rights reserved.
 *
 * NOTICE: All information including source code contained herein is, and
 * remains the sole property of Pentaho and its licensors. The intellectual
 * and technical concepts contained herein are proprietary and confidential
 * to, and are trade secrets of Pentaho and may be covered by U.S. and foreign
 * patents, or patents in process, and are protected by trade secret and
 * copyright laws. The receipt or possession of this source code and/or related
 * information does not convey or imply any rights to reproduce, disclose or
 * distribute its contents, or to manufacture, use, or sell anything that it
 * may describe, in whole or in part. Any reproduction, modification, distribution,
 * or public display of this information without the express written authorization
 * from Pentaho is strictly prohibited and in violation of applicable laws and
 * international treaties. Access to the source code contained herein is strictly
 * prohibited to anyone except those individuals and entities who have executed
 * confidentiality and non-disclosure agreements or other agreements with Pentaho,
 * explicitly covering such access.
 */
package com.pentaho.metaverse.analyzer.kettle.extensionpoints;

import com.pentaho.metaverse.analyzer.kettle.plugin.ExternalResourceConsumerPluginType;
import com.pentaho.metaverse.util.MetaverseUtil;
import org.pentaho.di.core.annotations.KettleLifecyclePlugin;
import org.pentaho.di.core.exception.KettlePluginException;
import org.pentaho.di.core.lifecycle.KettleLifecycleListener;
import org.pentaho.di.core.lifecycle.LifecycleException;
import org.pentaho.di.core.plugins.PluginInterface;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.job.entry.JobEntryBase;
import org.pentaho.di.trans.step.BaseStepMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * MetaverseKettleLifecycleHandler processes lifecycle events (startup, shutdown) in terms of managing the lineage
 * capabilities, such as creation of document controller(s), plugin map(s),
 */
@KettleLifecyclePlugin( id = "MetaverseKettleLifecycleHandler", name = "MetaverseKettleLifecycleHandler" )
public class MetaverseKettleLifecycleHandler implements KettleLifecycleListener {

  @Override
  public void onEnvironmentInit() throws LifecycleException {
    // Initialize the metaverse system (analyzers, locators, etc.)
    try {
      MetaverseUtil.initializeMetaverseObjects( new File(
          MetaverseKettleLifecycleHandler.class.getProtectionDomain().getCodeSource().getLocation().getPath() )
          .getParent() + "/plugin.spring.xml"
      );
    } catch ( Throwable t ) {
      // We probably don't have access to the application context or the Spring classes, so fall back to a
      // config file for injection of analyzers, etc.
      t.printStackTrace( System.err );
    }

    // Populate the map of steps to external resource consumer plugins
    loadExternalResourceConsumerMap();
  }

  @Override
  public void onEnvironmentShutdown() {
    // noop
  }

  protected void loadExternalResourceConsumerMap() throws LifecycleException {
    try {
      PluginRegistry registry = PluginRegistry.getInstance();
      List<PluginInterface> consumerPlugins = registry.getPlugins( ExternalResourceConsumerPluginType.class );
      for ( PluginInterface plugin : consumerPlugins ) {
        Object o = registry.loadClass( plugin );
        if ( o instanceof IStepExternalResourceConsumer ) {
          IStepExternalResourceConsumer consumer = (IStepExternalResourceConsumer) o;
          Class<? extends BaseStepMeta> stepMetaClass = consumer.getMetaClass();
          Map<Class<? extends BaseStepMeta>, List<IStepExternalResourceConsumer>> stepConsumerMap =
            ExternalResourceConsumerMap.getInstance().getStepConsumerMap();
          List<IStepExternalResourceConsumer> stepMetaConsumers = stepConsumerMap.get( stepMetaClass );
          if ( stepMetaConsumers == null ) {
            stepMetaConsumers = new ArrayList<IStepExternalResourceConsumer>();
            stepConsumerMap.put( stepMetaClass, stepMetaConsumers );
          }
          stepMetaConsumers.add( consumer );
        } else if ( o instanceof IJobEntryExternalResourceConsumer ) {
          IJobEntryExternalResourceConsumer consumer = (IJobEntryExternalResourceConsumer) o;
          Class<? extends JobEntryBase> jobMetaClass = consumer.getMetaClass();
          Map<Class<? extends JobEntryBase>, List<IJobEntryExternalResourceConsumer>> jobEntryConsumerMap =
            ExternalResourceConsumerMap.getInstance().getJobEntryConsumerMap();
          List<IJobEntryExternalResourceConsumer> jobEntryMetaConsumers = jobEntryConsumerMap.get( jobMetaClass );
          if ( jobEntryMetaConsumers == null ) {
            jobEntryMetaConsumers = new ArrayList<IJobEntryExternalResourceConsumer>();
            jobEntryConsumerMap.put( jobMetaClass, jobEntryMetaConsumers );
          }
          jobEntryMetaConsumers.add( consumer );
        }
      }
    } catch ( KettlePluginException kpe ) {
      throw new LifecycleException( kpe, true );
    }
  }
}