state = null;
memory_base_address = 0x00000000;

is_live = true;
current_state_index = 0;

function processInitialState(initial_state) {
    state = {}
    state.initial_state = initial_state;
    state.updates = [];
    is_live = true;
    current_state_index = 0;
    keyboard_enabled = false;
    document.getElementById("keyboard-switch").checked = false;

    computeState();
}

function processUpdate(update) {
    state.updates.push(update);
    if (is_live) {
        applyUpdate(update);
    }
}

function processState(current_state) {
    if (state == null) {
        state = current_state;
        is_live = true;
        current_state_index = 0;
        keyboard_enabled = false;
        document.getElementById("keyboard-switch").checked = false;
        computeState();
        return true;
    }
    return false;
}
function stepToStart() {
    while (current_state_index > 0) {
        stepBack(false);
    }
    updateUI();
}
function stepBack(needsUpdate=true) {
    if (current_state_index > 0) {
        unApplyUpdate(state.updates.find(update => update.index == current_state_index));
        current_state_index--;
        is_live = false;
    }
    if (needsUpdate) {
        updateUI();
    }
}

function stepForward(needsUpdate=true) {
    if (current_state_index == state.updates.length) {
        is_live = true;
        postAction("STEP", 0, 1);
    }
    if (current_state_index < state.updates.length) {
        current_state_index++;
        applyUpdate(state.updates.find(update => update.index == current_state_index));
        is_live = current_state_index == state.updates.length;
    }
    if (needsUpdate) {
        updateUI();
    }
}

function stepToEnd() {
    if (current_state_index == state.updates.length) {
        is_live = true;
        postAction("STEP", 0, 1);
    }
    while (current_state_index < state.updates.length) {
        stepForward(false);
    }
    updateUI();
}

function computeState() {
    state.current_state = state.initial_state;
    state.current_state.display_output = "";
    state.updates.forEach(update => {
        applyUpdate(update);
    });
}

function applyUpdate(update) {
    current_state_index = update.index
    state.current_state.pc = update.pc.new_value;
    state.current_state.display_output = update.display_output.new_value;
    for (const [registerNumber, registerUpdate] of Object.entries(update.register_updates)) {
        state.current_state.registers[registerNumber] = registerUpdate.new_value;
        updateRegisterValue(registerNumber, registerUpdate.new_value);
    }
    for (const [csrName, csrUpdate] of Object.entries(update.csr_updates)) {
        state.current_state.csrs[csrName] = csrUpdate.new_value;
        updateCSRValue(csrName, csrUpdate.new_value);
    }
    for (const [address, value] of Object.entries(update.memory_updates)) {
        state.current_state.memory[address] = value.new_value;
        updateMemoryValue(address, value.new_value);
    }
}

function unApplyUpdate(update) {
    state.current_state.pc = update.pc.old_value;
    state.display_output = update.display_output.old_value;
    for (const [registerNumber, registerUpdate] of Object.entries(update.register_updates)) {
        state.current_state.registers[registerNumber] = registerUpdate.old_value;
        updateRegisterValue(registerNumber, registerUpdate.old_value);
    }
    for (const [csrName, csrUpdate] of Object.entries(update.csr_updates)) {
        state.current_state.csrs[csrName] = csrUpdate.old_value;
        updateCSRValue(csrName, csrUpdate.old_value);
    }
    for (const [address, value] of Object.entries(update.memory_updates)) {
        state.current_state.memory[address] = value.old_value;
        updateMemoryValue(address, value.old_value);
    }
}

function storeBase(base) {
    if (base < 0x00000000) {
        base = 0x00000000;
    }
    if (base > 0xffffff00) {
        base = 0xffffff00;
    }
    memory_base_address = base;
    drawMemoryTab(base);
}

function stepMemBack() {
    if (memory_base_address > 0x00000100) {
        memory_base_address -= 0x00000100;
    } else {
        memory_base_address = 0x00000000;
    }
    drawMemoryTab(memory_base_address);
}

function stepMemForward() {
    if (memory_base_address < 0xffffff00) {
        memory_base_address += 0x00000100;
    } else {
        memory_base_address = 0xffffff00;
    }
    drawMemoryTab(memory_base_address);
}