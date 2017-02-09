/**
 *  Barn Automation
 *
 *  Copyright 2017 Rob Whapham
 *
 */
definition(
    name: "Barn Automation",
    namespace: "rwhapham",
    author: "Rob Whapham",
    description: "Automation for various devices in the barn",
    category: "Convenience",
    iconUrl: "https://github.com/rwhapham/smartthings/raw/master/BarnAutomation/resources/9014-128x128x32.png",
    iconX2Url: "https://github.com/rwhapham/smartthings/raw/master/BarnAutomation/resources/9014-256x256x32.png",
    iconX3Url: "https://github.com/rwhapham/smartthings/raw/master/BarnAutomation/resources/9014-256x256x32.png")


preferences {
	page(name: "page1", title: "Setup", nextPage: "page2", install: false, uninstall: true)
    {
        section("Audio notification on open:")
        {
            input "notifyOnOpenSensors", "capability.contactSensor", title: "Which?", multiple: true, required: false
        }

        section("Audio notification on close:")
        {
            input "notifyOnCloseSensors", "capability.contactSensor", title: "Which?", multiple: true, required: false
        }

		section("Monitor things left open:")
        {
            input "leftOpenSensors", "capability.contactSensor", title: "Which?", multiple: true, required: false
            input "leftOpenDuration", "number", title: "For how long (mins)?", defaultValue: 15, range: "1..30", required: false
        }

        section("Monitor temperature:")
        {
            input "tempSensors", "capability.temperatureMeasurement", title: "Which?", multiple: true, required: false
            input "tempLow", "number", title: "For temperatures below?", defaultValue: 35, range: "0..100", required: false
            input "tempHigh", "number", title: "For temperatures above?", defaultValue: 85, range: "0..100", required: false
        }
        
        section("Doorbell notification:")
        {
            input "buttonDevice", "capability.button", title: "Which?", required: false
        }
        
        section("Knock notification:")
        {
            input "knockSensors", "capability.accelerationSensor", title: "Which?", multiple: true, required: false
            input "knockDelay", "number", title: "Doorbell delay (secs)?", defaultValue: 3, range: "0..10", required: false
            input "knockDoorbellDevice", "capability.switch", title: "Trigger doorbell?", required: false
        }
        
        section("Audio notification player:")
        {
            input "audioDevice", "capability.audioNotification", title: "Which?", required: false
        }
    }
    
    page(name: "page2", title: "Open/Close Audio Notifications", nextPage: "page3", install: false, uninstall: true)
    page(name: "page3", title: "Left Open Audio Notifications", nextPage: "page4", install: false, uninstall: true)
    page(name: "page4", title: "Temperature Warning Audio Notifications", install: true, uninstall: true)
}

def page2() {
	dynamicPage(name: "page2")
    {
    	if (audioDevice)
        {
            if (notifyOnOpenSensors || notifyOnCloseSensors)
            {
                def pos = 0

                notifyOnOpenSensors.each {
                    section("$it.displayName open notification:")
                    {
                        input "onOpenAudioTrack" + pos, "text", title: "Audio track?", required: false
                    }

                    pos++
                }

                pos = 0

                notifyOnCloseSensors.each {
                    section("$it.displayName close notification:")
                    {
                        input "onCloseAudioTrack" + pos, "text", title: "Audio track?", required: false
                    }

                    pos++
                }
            }
            else
            {
                section("No open/close devices selected")
            }
        }
        else
        {
        	section("No audio notification player selected")
        }
	}
}

def page3() {
	dynamicPage(name: "page3")
    {
    	if (audioDevice)
        {
            if (leftOpenSensors)
            {
                def pos = 0

                leftOpenSensors.each {
                    section("$it.displayName left open notification:")
                    {
                        input "leftOpenAudioTrack" + pos, "text", title: "Audio track?", required: false
                    }

                    pos++
                }
            }
            else
            {
                section("No left open devices selected")
            }
        }
        else
        {
        	section("No audio notification player selected")
        }
	}
}

def page4() {
	dynamicPage(name: "page4")
    {
    	if (audioDevice)
        {
            if (tempSensors)
            {
                def pos = 0

                tempSensors.each {
                    section("$it.displayName temperature notifications:")
                    {
                        input "lowTempAudioTrack" + pos, "text", title: "Low temp audio track?", required: false
                        input "highTempAudioTrack" + pos, "text", title: "High temp audio track?", required: false
                    }

                    pos++
                }
            }
            else
            {
                section("No temperature sensors selected")
            }
        }
        else
        {
        	section("No audio notification player selected")
        }
    }
}

def installed() {
	log.debug "Installed with settings: ${settings}"

	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"

	unschedule()
	unsubscribe()
	initialize()
}

def initialize() {
	if (notifyOnOpenSensors)
    	subscribe(notifyOnOpenSensors, "contact.open", onOpenHandler)
        
    if (notifyOnCloseSensors)
    	subscribe(notifyOnCloseSensors, "contact.closed", onClosedHandler)

	if (leftOpenSensors)
    {
    	leftOpenSensors.each {
        	if (it.currentValue("contact") == "open")
            {
                log.debug "$it.displayName is currently open, monitoring for left open"

                runIn((leftOpenDuration) ? leftOpenDuration * 60 : 15 * 60, leftOpenHandler, [overwrite: false, data: [deviceId: it.id]])
            }
        }
        
		subscribe(leftOpenSensors, "contact.open", startLeftOpenHandler)
    }
    
    if (tempSensors)
		subscribe(tempSensors, "temperature", temperatureHandler)
    
    if (buttonDevice)
    	subscribe(buttonDevice, "button", doorbellHandler)
        
    if (knockSensors)
    	subscribe(knockSensors, "acceleration.active", knockHandler)
}

def onOpenHandler(evt)
{
	log.debug "$evt.displayName was opened"
    
    def pos = notifyOnOpenSensors.findIndexOf { it.id == evt.deviceId }
    
    if (audioDevice && (pos >= 0))
    {
        def audioTrackName = "onOpenAudioTrack" + pos
        def audioTrack = this."$audioTrackName"
        
        if (audioTrack)
        {
            log.info "onOpenHandler - $audioDevice.displayName play track $audioTrack"
        }
    }
}

def onClosedHandler(evt)
{
	log.debug "$evt.displayName was closed"
    
    def pos = notifyOnCloseSensors.findIndexOf { it.id == evt.deviceId }
    
	if (audioDevice && (pos >= 0))
    {
        def audioTrackName = "onCloseAudioTrack" + pos
        def audioTrack = this."$audioTrackName"

		if (audioTrack)
        {
            log.info "onOpenHandler - $audioDevice.displayName play track $audioTrack"
        }
    }
}

def startLeftOpenHandler(evt)
{
	log.debug "$evt.displayName was opened, monitoring for left open"

	runIn((leftOpenDuration) ? leftOpenDuration * 60 : 15 * 60, leftOpenHandler, [overwrite: false, data: [deviceId: evt.deviceId]])
}

def leftOpenHandler(data)
{
    def pos = leftOpenSensors.findIndexOf { it.id == data.deviceId }

    if (pos >= 0)
    {
    	def device = leftOpenSensors[pos]
        def contactState = device.currentState("contact")

        if (contactState.value == "open")
        {
            def elapsed = now() - contactState.rawDateCreated.time

            if (elapsed >= ((leftOpenDuration * 60000) - 1000))
            {
                log.info "$device.displayName was left open"

                sendPush ("$device.displayName was left open")

				if (audioDevice)
                {
                    def audioTrackName = "leftOpenAudioTrack" + pos
                    def audioTrack = this."$audioTrackName"

                    if (audioTrack)
                    {
                        log.info "leftOpenHandler - $audioDevice.displayName play track $audioTrack"
                    }
                }
            }
            else
            {
                log.debug "$device.displayName was not opened long enough since last check"
            }
        }
        else
        {
            log.debug "$device.displayName was closed before threshold elapsed"
        }
    }
}

def temperatureHandler(evt)
{
    def pos = tempSensors.findIndexOf { it.id == evt.deviceId }
    
    if (pos >= 0)
    {
        log.debug "temperature from $evt.displayName: $evt.value"

        def recentTempEvents = evt.device.events ()?.findAll { it.name == "temperature" }
        def actualTempLow = (tempLow) ? tempLow : 35
        def actualTempHigh = (tempHigh) ? tempHigh : 85
        def lastTemp = (recentTempEvents && (recentTempEvents.size () > 1)) ? recentTempEvents[1].doubleValue : -1
        def prevTemp = (recentTempEvents && (recentTempEvents.size () > 2)) ? recentTempEvents[2].doubleValue : -1

        log.debug "last temperatures from $evt.displayName: $lastTemp, $prevTemp"

        if ((evt.doubleValue <= actualTempLow) && ((lastTemp == -1) || (lastTemp > actualTempLow)) && ((prevTemp == -1) || (prevTemp > actualTempLow)))
        {
            sendPush("Low temperature warning from $evt.displayName: $evt.value")

            if (audioDevice)
            {
                def audioTrackName = "lowTempAudioTrack" + pos
                def audioTrack = this."$audioTrackName"

                if (audioTrack)
                {
                    log.info "temperatureHandler (low) - $audioDevice.displayName play track $audioTrack"
                }
            }
        }

        if ((evt.doubleValue >= actualTempHigh) && ((lastTemp == -1) || (lastTemp < actualTempHigh)) && ((lastTemp == -1) || (lastTemp < actualTempHigh)))
        {
            sendPush("High temperature warning from $evt.displayName: $evt.value")

            if (audioDevice)
            {
                def audioTrackName = "highTempAudioTrack" + pos
                def audioTrack = this."$audioTrackName"

                if (audioTrack)
                {
                    log.info "temperatureHandler (high) - $audioDevice.displayName play track $audioTrack"
                }
            }
        }
	}
}

def doorbellHandler(evt)
{
	log.info "$evt.displayName was pressed"
    
    sendPushMessage("$evt.displayName was pressed")
}

def knockHandler(evt)
{
	log.debug "$evt.displayName was active"
    
	runIn((knockDelay) ? knockDelay : 3, doorKnock, , [overwrite: false, data: [deviceId: evt.deviceId, time: evt.date.time]])
}

def doorKnock(data)
{
	def pos = knockSensors.findIndexOf { it.id == data.deviceId }
    
	if (pos >= 0)
    {
    	def device = knockSensors[pos]
        
        try
        {
        	if (device.latestValue("contact") == "closed")
            {
            	def recentEvents = device.eventsSince (new Date (data.time - 2000))
                
                if (recentEvents)
                {
                	def lastOpenEvent = recentEvents.find { (it.name == "status") && (it.value == "open") }
                	def lastClosedEvent = recentEvents.find { (it.name == "status") && (it.value == "closed") }
                    
                    if ((lastOpenEvent == null) && (lastClosedEvent == null))
                    {
                        log.info "doorKnock - knocking at $device.displayName"

                        //sendPushMessage("Knocking at $device.displayName")

                        if (knockDoorbellDevice)
                        {
                            //knockDoorbellDevice.on ()
                        }
                    }
                    else
                    {
                        log.debug "doorKnock - $device.displayName was opened or closed; no knock"
                    }
                }
                else
                {
                	log.warn "doorKnock - no recent events found for $device.displayName"
                }
            }
            else
            {
            	log.debug "doorKnock - $device.displayName is open; no knock"
            }
        }
        catch (e)
        {
        	log.error ("doorKnock - caught exception", e)
        }
    }
}
