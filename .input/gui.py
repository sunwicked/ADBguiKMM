import os
import subprocess
import tkinter
from datetime import datetime
from time import sleep
from tkinter import filedialog

BLACK = "#000000"

PORT_NUMBER = "5555"

SECS = 5

BUTTON_WIDTH = 20
BUTTON_HEIGHT = 2

font_size = 14
numberOfScreenUnits = 80

dir_path = os.path.dirname(os.path.realpath(__file__))
log_cat_file_path = dir_path + "/" + str(datetime.now().microsecond) + ".txt"
ip = "0.0.0.0:5555"
package = "test"
log = ""
push_location = "/sdcard/"

top = tkinter.Tk()


def disconnect_call_back():
    console_log = subprocess.run(['adb', 'disconnect'], stdout=subprocess.PIPE)
    convert_to_str(console_log)


def devices_call_back():
    console_log = subprocess.run(['adb', 'devices'], stdout=subprocess.PIPE)
    convert_to_str(console_log)


def connect_call_back():
    global ip
    ip = entry.get()
    ip_value['text'] = ip + ":" + PORT_NUMBER
    console_log = subprocess.run(['adb', 'connect', ip], stdout=subprocess.PIPE)
    convert_to_str(console_log)


def reboot_call_back():
    if is_device_connected():
        subprocess.Popen(['adb', 'reboot'],
                         stdout=subprocess.PIPE)
        log_value['text'] = "Rebooting   device"
    else:
        log_value['text'] = "No devices found"


def pick_apk_callback():
    if is_device_connected():
        filename = filedialog.askopenfilename(initialdir="/", title="Select file")
        global package
        proc = subprocess.Popen(['aapt', 'dump', 'badging', filename],
                                stdout=subprocess.PIPE)
        package = proc.stdout.readline()
        proc.terminate()
        std_out = str(package)
        if std_out:
            package = std_out.split("versionCode")[0]
            package = package.split("'")[1]
            package_value['text'] = package
            subprocess.Popen(
                ['adb', 'install', filename],
                stdout=subprocess.PIPE)
            log_value['text'] = "Installing  apk... Check app list"
        else:
            print("failed")
    else:
        log_value['text'] = "No devices found"


def push_file_callback():
    if is_device_connected():
        filename = filedialog.askopenfilename(initialdir="/", title="Select file")
        subprocess.run(
            ['adb', 'push', filename, push_location],
            stdout=subprocess.PIPE)
    else:
        log_value['text'] = "No devices found"


def screen_shot_callback():
    if is_device_connected():
        file_name = str(datetime.now().microsecond)
        pull_location = "/sdcard" + "/" + file_name + ".png"
        file_path = dir_path + "/" + file_name + ".png"
        subprocess.run(['adb', 'shell', 'screencap', pull_location],
                       stdout=subprocess.PIPE)
        subprocess.run(
            ['adb', 'pull', pull_location, file_path],
            stdout=subprocess.PIPE)
        log_value['text'] = "Screenshot:" + file_path
    else:
        log_value['text'] = "No devices found"


def record_start_callback():
    if is_device_connected():
        file_name = str(datetime.now().microsecond)
        global pull_location
        pull_location = "/sdcard" + "/" + file_name + ".mp4"
        global file_path
        file_path = dir_path + "/" + file_name + ".mp4"
        global proc_record
        proc_record = subprocess.Popen(['adb', 'shell', 'screenrecord', pull_location],
                                       stdout=subprocess.PIPE)
        log_value['text'] = "Recording started"
    else:
        log_value['text'] = "No devices found"


def record_stop_callback():
    if is_device_connected():
        global pull_location
        global file_path
        global proc_record
        proc_record.terminate()
        sleep(SECS)
        subprocess.run(
            ['adb', 'pull', pull_location, file_path],
            stdout=subprocess.PIPE)
        log_value['text'] = file_path
    else:
        log_value['text'] = "No devices found"


def uninstall_call_back():
    if is_device_connected():
        if package != "test":
            console_log = subprocess.run(['adb', 'uninstall', package], stdout=subprocess.PIPE)
            convert_to_str(console_log)
        elif entry_package.get():
            console_log = subprocess.run(['adb', 'uninstall', entry_package.get()], stdout=subprocess.PIPE)
            convert_to_str(console_log)
        else:
            log_value['text'] = "Incorrect package name test"
    else:
        log_value['text'] = "No devices found"


def log_cat_to_file_callback():
    if is_device_connected():
        global log_cat_file_path
        log_cat_file_path = dir_path + "/" + str(datetime.now().microsecond) + ".txt"
        with open(log_cat_file_path, "wb", 0) as out:
            log_cat_stop['state'] = 'normal'
            global proc
            proc = subprocess.Popen('adb logcat', stdout=out, shell=True)
            log_value['text'] = "Logging to" + log_cat_file_path
    else:
        log_value['text'] = "No devices found"


def log_cat_stop_to_file_callback():
    global proc
    proc.terminate()
    global log_cat_file_path
    log_value['text'] = "Logging stopped " + log_cat_file_path
    log_cat_stop['state'] = 'disabled'


def clear_call_back():
    console_log = subprocess.run(['adb', 'shell', 'pm', 'clear', package], stdout=subprocess.PIPE)
    convert_to_str(console_log)


def convert_to_str(input_val):
    log_value['text'] = str(input_val.stdout)


def is_device_connected():
    console_log = subprocess.run(['adb', 'devices'], stdout=subprocess.PIPE)
    val = str(console_log.stdout)
    return val.__len__() > 35  # hacky to fix


# UI for label and  button
font_style = ('Helvetica', font_size, 'bold')
label = tkinter.Label(top, text=" Enter TV IP:: ", font=font_style)
label.grid(row=0, column=0)

entry = tkinter.Entry(top)
entry.grid(row=0, column=1)

connect = tkinter.Button(top, text="ADB connect", width=BUTTON_WIDTH, height=BUTTON_HEIGHT, command=connect_call_back)
connect.grid(row=0, column=2)
pick = tkinter.Button(top, bg=BLACK, text=" ADB install ", width=BUTTON_WIDTH, height=BUTTON_HEIGHT,
                      command=pick_apk_callback)
pick.grid(row=2, column=0)

disconnect = tkinter.Button(top, bg=BLACK, text=" ADB disconnect ", width=BUTTON_WIDTH, height=BUTTON_HEIGHT,
                            command=disconnect_call_back)
disconnect.grid(row=2, column=2)

reboot = tkinter.Button(top, bg=BLACK, text=" ADB reboot ", width=BUTTON_WIDTH, height=BUTTON_HEIGHT,
                        command=reboot_call_back)
reboot.grid(row=4, column=0)
clear = tkinter.Button(top, bg=BLACK, text=" ADB clear ", width=BUTTON_WIDTH, height=BUTTON_HEIGHT,
                       command=clear_call_back)
clear.grid(row=4, column=1)

push = tkinter.Button(top, bg=BLACK, text=" ADB push ", width=BUTTON_WIDTH, height=BUTTON_HEIGHT,
                      command=push_file_callback)
push.grid(row=4, column=2)

devices = tkinter.Button(top, bg=BLACK, text=" ADB devices ", width=BUTTON_WIDTH, height=BUTTON_HEIGHT,
                         command=devices_call_back)
devices.grid(row=2, column=1)
uninstall = tkinter.Button(top, bg=BLACK, text=" ADB uninstall ", width=BUTTON_WIDTH, height=BUTTON_HEIGHT,
                           command=uninstall_call_back)

log_cat = tkinter.Button(top, bg=BLACK, text=" ADB logcat ", width=BUTTON_WIDTH, height=BUTTON_HEIGHT,
                         command=log_cat_to_file_callback)
log_cat.grid(row=6, column=0)
log_cat_stop = tkinter.Button(top, bg=BLACK, text=" ADB logcat stop ", width=BUTTON_WIDTH, height=BUTTON_HEIGHT,
                              state="disabled",
                              command=log_cat_stop_to_file_callback)
log_cat_stop.grid(row=6, column=1)

uninstall.grid(row=7, column=0)
entry_package = tkinter.Entry(top)
entry_package.grid(row=7, column=1)

screen_shot = tkinter.Button(top, bg=BLACK, text=" ADB Screenshot ", width=BUTTON_WIDTH, height=BUTTON_HEIGHT,
                             command=screen_shot_callback)
screen_shot.grid(row=9, column=0)

record = tkinter.Button(top, bg=BLACK, text=" ADB Record Start ", width=BUTTON_WIDTH, height=BUTTON_HEIGHT,
                        command=record_start_callback)
record.grid(row=10, column=0)

record_stop = tkinter.Button(top, bg=BLACK, text=" ADB Record Stop ", width=BUTTON_WIDTH, height=BUTTON_HEIGHT,
                             command=record_stop_callback)
record_stop.grid(row=10, column=1)

package_label = tkinter.Label(top, text=" Package Name::", font=font_style)
package_label.grid(row=13, column=0)
package_value = tkinter.Label(top, text=package)
package_value.grid(row=13, column=1)

ip_label = tkinter.Label(top, text=" IP::", font=font_style)
ip_label.grid(row=14, column=0)
ip_value = tkinter.Label(top, text=ip)
ip_value.grid(row=14, column=1)

log_label = tkinter.Label(top, text=" Log::", font=font_style, wraplength=numberOfScreenUnits)
log_label.grid(row=15, column=0)
log_value = tkinter.Label(top, text=log)
log_value.grid(row=15, column=1)

top.mainloop()
