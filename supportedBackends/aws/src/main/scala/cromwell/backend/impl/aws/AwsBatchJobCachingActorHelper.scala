/*
 * Copyright 2018 Amazon.com, Inc. or its affiliates.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *
 *  3. Neither the name of the copyright holder nor the names of its
 *  contributors may be used to endorse or promote products derived from
 *  this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *  "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 *  BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 *  FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 *  THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 *  INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 *  HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 *  STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
 *  IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 *  POSSIBILITY OF SUCH DAMAGE.
 */

package cromwell.backend.impl.aws

import akka.actor.Actor
import cromwell.backend.impl.aws.io.{AwsBatchVolume, AwsBatchWorkingDisk}
import cromwell.backend.standard.StandardCachingActorHelper
import cromwell.core.labels.Labels
import cromwell.core.logging.JobLogging
import cromwell.core.path.Path
import cromwell.services.metadata.CallMetadataKeys

import scala.language.postfixOps

trait AwsBatchJobCachingActorHelper extends StandardCachingActorHelper {
  this: Actor with JobLogging =>

  lazy val initializationData: AwsBatchBackendInitializationData = {
    backendInitializationDataAs[AwsBatchBackendInitializationData]
  }

  lazy val configuration: AwsBatchConfiguration = initializationData.configuration

  // TODO: Determine if call paths are relevant
  lazy val callPaths: AwsBatchJobPaths = jobPaths.asInstanceOf[AwsBatchJobPaths]

  lazy val runtimeAttributes = AwsBatchRuntimeAttributes(validatedRuntimeAttributes, configuration.runtimeConfig)

  lazy val workingDisk: AwsBatchVolume = runtimeAttributes.disks.find(_.name == AwsBatchWorkingDisk.Name).get

  lazy val callRootPath: Path = callPaths.callExecutionRoot
  lazy val returnCodeFilename: String = callPaths.returnCodeFilename
  // lazy val returnCodePath: Path = callPaths.returnCode
  lazy val logFilename: String = callPaths.logFilename

  lazy val attributes: AwsBatchAttributes = configuration.batchAttributes

  override protected def nonStandardMetadata: Map[String, Any] = {
    Map(
      AwsBatchMetadataKeys.ExecutionBucket -> initializationData.workflowPaths.executionRootString,
    )
  }
}