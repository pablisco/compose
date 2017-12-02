package compose

import javax.annotation.processing.ProcessingEnvironment

interface ProcessEnvironmentAware {

    val environment: ProcessingEnvironment

}

