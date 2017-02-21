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
            input "notifyOnOpenToneDevice", "capability.tone", title: "Trigger beep?", required: false
        }

        section("Audio notification on close:")
        {
            input "notifyOnCloseSensors", "capability.contactSensor", title: "Which?", multiple: true, required: false
            input "notifyOnCloseToneDevice", "capability.tone", title: "Trigger beep?", required: false
        }

		section("Monitor things left open:")
        {
            input "leftOpenSensors", "capability.contactSensor", title: "Which?", multiple: true, required: false
            input "leftOpenDuration", "number", title: "For how long (mins)?", defaultValue: 15, range: "1..30", required: false
            input "leftOpenToneDevice", "capability.tone", title: "Trigger beep?", required: false
        }

        section("Monitor temperature:")
        {
            input "tempSensors", "capability.temperatureMeasurement", title: "Which?", multiple: true, required: false
            input "tempLow", "number", title: "For temperatures below?", defaultValue: 35, range: "0..100", required: false
            input "tempHigh", "number", title: "For temperatures above?", defaultValue: 85, range: "0..100", required: false
            input "tempAlarmDevice", "capability.alarm", title: "Trigger alarm?", required: false
        }
        
        section("Doorbell notification:")
        {
            input "buttonDevice", "capability.button", title: "Which?", required: false
            input "doorbellPausePlayers", "capability.musicPlayer", title: "Pause playback on?", multiple: true, required: false
        }
        
        section("Knock notification:")
        {
            input "knockSensors", "capability.accelerationSensor", title: "Which?", multiple: true, required: false
            input "knockDelay", "number", title: "Doorbell delay (secs)?", defaultValue: 3, range: "0..10", required: false
            input "knockDoorbellDevice", "capability.switch", title: "Trigger doorbell?", required: false
        }
        
        section("Monitor smoke/CO2:")
        {
        	input "smokeSensors", "capability.smokeDetector", title: "Which?", multiple: true, required: false
            input "smokeStopPlayers", "capability.musicPlayer", title: "Stop playback on?", multiple: true, required: false
        }
        
        section("Monitor leaks:")
        {
        	input "waterSensors", "capability.waterSensor", title: "Which?", multiple: true, required: false
            input "waterStopPlayers", "capability.musicPlayer", title: "Stop playback on?", multiple: true, required: false
            input "waterAlarmDevice", "capability.alarm", title: "Trigger alarm?", required: false
        }
        
        section("Audio notification player:")
        {
            input "audioDevice", "capability.audioNotification", title: "Which?", required: false
            input "audioDeviceVolume", "number", title: "Notification volume?", defaultValue: 8, range: "1..10", required: false
        }
    }
    
    page(name: "page2", title: "Open/Close Audio Notifications", nextPage: "page3", install: false, uninstall: true)
    page(name: "page3", title: "Left Open Audio Notifications", nextPage: "page4", install: false, uninstall: true)
    page(name: "page4", title: "Temperature Warning Audio Notifications", nextPage: "page5", install: false, uninstall: true)
    page(name: "page5", title: "Water Leak Audio Notifications", install: true, uninstall: true)
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
                {
                	paragraph "No open/close devices selected"
                }
            }
        }
        else
        {
        	section()
            {
            	paragraph "No audio notification player selected"
            }
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
                section()
                {
					paragraph "No left open devices selected"
                }
            }
        }
        else
        {
        	section()
            {
            	paragraph "No audio notification player selected"
            }
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
                section()
                {
                	paragraph "No temperature sensors selected"
                }
            }
        }
        else
        {
        	section()
            {
            	paragraph "No audio notification player selected"
            }
        }
    }
}

def page5() {
	dynamicPage(name: "page5")
    {
    	if (audioDevice)
        {
            if (waterSensors)
            {
                def pos = 0

                waterSensors.each {
                    section("$it.displayName leak notification:")
                    {
                        input "leakAudioTrack" + pos, "text", title: "Audio track?", required: false
                    }

                    pos++
                }
            }
            else
            {
                section()
                {
                	paragraph "No water sensors selected"
                }
            }
        }
        else
        {
        	section()
            {
            	paragraph "No audio notification player selected"
            }
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
	state.triggerDoorbell = false
    
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
    	subscribe(buttonDevice, "button.pushed", doorbellHandler)
        
    if (knockSensors)
    	subscribe(knockSensors, "acceleration.active", knockHandler)
        
    if (smokeSensors)
    {
    	subscribe(smokeSensors, "smoke", smokeHandler)
        subscribe(smokeSensors, "carbonMonoxide", smokeHandler)
    }
    
    if (waterSensors)
    	subscribe(waterSensors, "water", waterHandler)
}

def playNotificationTrack(data)
{
	if (audioDevice && data.audioTrack)
    {
        log.info "playNotificationTrack - $audioDevice.displayName play track $data.audioTrack"

        audioDevice.playTrackAtVolume(data.audioTrack, audioDeviceVolume ?: 8)
    }
}

def onOpenHandler(evt)
{
	def tonePlayed = false
    
	log.debug "$evt.displayName was opened"
    
    if (notifyOnOpenToneDevice)
    {
    	notifyOnOpenToneDevice.beep()
        tonePlayed = true
    }
    
    if (audioDevice)
    {
        def pos = notifyOnOpenSensors.findIndexOf { it.id == evt.deviceId }
        
        if (pos >= 0)
        {
            def audioTrackName = "onOpenAudioTrack" + pos
            def audioTrack = this."$audioTrackName"

            if (audioTrack)
            {
                if (tonePlayed)
                {
					runIn(2, playNotificationTrack, [overwrite: false, data: [audioTrack: audioTrack]])
                }
                else
                {
                	playNotificationTrack([audioTrack: audioTrack])
                }
            }
        }
    }
}

def onClosedHandler(evt)
{
	def tonePlayed = false
    
	log.debug "$evt.displayName was closed"
    
    if (notifyOnOpenToneDevice)
    {
    	notifyOnOpenToneDevice.beep()
        tonePlayed = true
    }
    
    if (audioDevice)
    {
        def pos = notifyOnCloseSensors.findIndexOf { it.id == evt.deviceId }
        
        if (pos >= 0)
        {
            def audioTrackName = "onCloseAudioTrack" + pos
            def audioTrack = this."$audioTrackName"

            if (audioTrack)
            {
                if (tonePlayed)
                {
					runIn(2, playNotificationTrack, [overwrite: false, data: [audioTrack: audioTrack]])
                }
                else
                {
                	playNotificationTrack([audioTrack: audioTrack])
                }
            }
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
            	def tonePlayed = false
                
                log.info "$device.displayName was left open"

                sendPush ("$device.displayName was left open")
                
                if (leftOpenToneDevice)
                {
                	leftOpenToneDevice.beep()
                    tonePlayed = true
                }

				if (audioDevice)
                {
                    def audioTrackName = "leftOpenAudioTrack" + pos
                    def audioTrack = this."$audioTrackName"

                    if (audioTrack)
                    {
                        if (tonePlayed)
                        {
                            runIn(2, playNotificationTrack, [overwrite: false, data: [audioTrack: audioTrack]])
                        }
                        else
                        {
                            playNotificationTrack([audioTrack: audioTrack])
                        }
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
        def playTone = false
        def audioTrackName = null
        def lowAudioTrack = null
        def highAudioTrack = null
        def audioDelay = 0

        log.debug "last temperatures from $evt.displayName: $lastTemp, $prevTemp"

        if ((evt.doubleValue <= actualTempLow) && ((lastTemp == -1) || (lastTemp > actualTempLow)) && ((prevTemp == -1) || (prevTemp > actualTempLow)))
        {
			sendPush("Low temperature warning from $evt.displayName: $evt.value")

			playTone = true
			audioTrackName = "lowTempAudioTrack" + pos
			lowAudioTrack = this."$audioTrackName"
        }

        if ((evt.doubleValue >= actualTempHigh) && ((lastTemp == -1) || (lastTemp < actualTempHigh)) && ((lastTemp == -1) || (lastTemp < actualTempHigh)))
        {
            sendPush("High temperature warning from $evt.displayName: $evt.value")

			playTone = true
			audioTrackName = "highTempAudioTrack" + pos
			highAudioTrack = this."$audioTrackName"
        }
        
        if (playTone && tempAlarmDevice)
        {
        	tempAlarmDevice.siren()
            audioDelay += 2
        }
        
        if (audioDevice && (lowAudioTrack || highAudioTrack))
        {
            if (lowAudioTrack)
            {
                if (audioDelay > 0)
                {
                    runIn(audioDelay, playNotificationTrack, [overwrite: false, data: [audioTrack: lowAudioTrack]])
                }
                else
                {
                    playNotificationTrack([audioTrack: lowAudioTrack])
                }
                
                audioDelay += 3
            }

            if (highAudioTrack)
            {
                if (audioDelay > 0)
                {
                    runIn(audioDelay, playNotificationTrack, [overwrite: false, data: [audioTrack: highAudioTrack]])
                }
                else
                {
                    playNotificationTrack([audioTrack: highAudioTrack])
                }
            }
		}
	}
}

def doorbellHandler(evt)
{
	if (doorbellPausePlayers)
    {
    	doorbellPausePlayers.each {
        	if (it.latestValue("status") == "playing")
            {
            	log.info "doorbellHandler - pausing $it.displayName"
                
            	it.pause()
            }
        }
    }
    
	if (state.triggerDoorbell == false)
    {
        log.info "$evt.displayName was pressed"

        sendPushMessage("$evt.displayName was pressed")
    }
    else
    {
    	state.triggerDoorbell = false
    }
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

                        sendPushMessage("Knocking at $device.displayName")

                        if (knockDoorbellDevice)
                        {
                        	state.triggerDoorbell = true
                            
                            knockDoorbellDevice.on ()
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

def smokeHandler(evt)
{
    log.debug "smokeHandler - $evt.displayName reported $evt.value"

	if (evt.value != "clear")
    {
    	if (smokeStopPlayers)
        {
            smokeStopPlayers.each {
                if (it.latestValue("status") == "playing")
                {
                    log.info "smokeHandler - stopping $it.displayName"

                    it.stop()
                }
            }
        }
    }
}

def waterHandler(evt)
{
    log.debug "waterHandler - $evt.displayName reported $evt.value"
    
	if (evt.value != "dry")
    {
        def tonePlayed = false

        sendPush("Leak detected by $evt.displayName")
        
		if (waterStopPlayers)
        {
            waterStopPlayers.each {
                if (it.latestValue("status") == "playing")
                {
                    log.info "waterHandler - stopping $it.displayName"

                    it.stop()
                }
            }
        }
        
        if (waterAlarmDevice)
        {
            waterAlarmDevice.siren()
            tonePlayed = true
        }

        if (audioDevice)
        {
            def pos = waterSensors.findIndexOf { it.id == evt.deviceId }

            if (pos >= 0)
            {
                def audioTrackName = "leakAudioTrack" + pos
                def audioTrack = this."$audioTrackName"

                if (audioTrack)
                {
                    if (tonePlayed)
                    {
                        runIn(2, playNotificationTrack, [overwrite: false, data: [audioTrack: audioTrack]])
                    }
                    else
                    {
                        playNotificationTrack([audioTrack: audioTrack])
                    }
                }
            }
        }
    }
}
