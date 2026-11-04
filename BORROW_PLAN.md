# Lyra-Code Improvement Plan: Borrowing from Agora

## Philosophy
Combine Agora's best architectural patterns with Lyra-Code's unique features (email, WebDAV, mini server, Termux, curated themes, profiles). Hybrid approach: keep what's good, borrow what's better, add what's missing.

---

## Phase 1: Core Improvements (High Priority)

### 1.1 Dynamic Theme System (Borrow: MaterialKolor)
**What**: Replace static theme presets with dynamic HCT-based color schemes
**How**: 
- Add `com.materialkolor` dependency
- Create `DynamicThemeEngine` that generates ColorScheme from seed color + style
- Keep curated presets as "quick picks" that set seed color
- Add custom seed color picker
- Add AMOLED background toggle (pure black)
- Keep hardcoded IconColor for icons (don't change with theme)
**Files**: UiCommon.kt, SettingsAppearanceScreens.kt, AppSettings.kt
**Effort**: Medium

### 1.2 Token-Budget Context Compaction (Borrow: Agora)
**What**: Intelligent context management with token budgets
**How**:
- Add token counter (estimate tokens per message)
- Implement context budget slider (4K-1M tokens)
- Add "Compact" button that creates non-destructive summary capsule
- Retain verbatim recent suffix (last N messages unchanged)
- Store compact capsules in conversation metadata
**Files**: ChatController.kt, ChatRequestComposition.kt, AppSettings.kt
**Effort**: High

### 1.3 Message Branching (Borrow: Agora)
**What**: Tree-structured conversations with edit/regenerate
**How**:
- Add `parentMessageId` and `branchIndex` to message model
- UI: swipe to see alternative branches
- Regenerate creates new branch, doesn't delete old
- Branch navigator in chat UI
**Files**: data models, ChatMessages.kt, ChatController.kt
**Effort**: High

---

## Phase 2: Advanced Features (Medium Priority)

### 2.1 PRoot Sandbox (Borrow: Agora, Hybrid with Termux)
**What**: Safe code execution environment
**How**:
- Option A: Bundle PRoot binaries (F-Droid flavor) - like Agora
- Option B: Use existing Termux as sandbox (Lyra-Code advantage)
- Option C: Hybrid - Termux for dev, PRoot for sandboxed execution
- Add sandbox toggle in settings
- Sandboxed file system access
**Files**: termux/, new sandbox/ module
**Effort**: High

### 2.2 ChatGPT/Claude Import (Borrow: Agora)
**What**: Import conversations from other AI apps
**How**:
- Parse ChatGPT export JSON format
- Parse Claude export format
- Convert to Lyra-Code conversation model
- Add import button in settings
**Files**: data/import/, SettingsScreens.kt
**Effort**: Medium

### 2.3 Scheduled Backups (Borrow: Agora)
**What**: Automatic periodic backups
**How**:
- Use WorkManager for scheduled backup
- Configurable frequency (daily/weekly)
- Backup to local storage or WebDAV
- Notification on completion
**Files**: data/backup/, SettingsScreens.kt
**Effort**: Medium

### 2.4 Multi-Language Support (Borrow: Agora)
**What**: 12+ interface languages
**How**:
- Extract all strings to resources
- Add language selector in settings
- Support: en, zh, ja, ko, es, fr, de, pt, ru, ar, hi, tr
- Use Android's locale configuration
**Files**: res/values-*/, AppStrings.kt, SettingsScreens.kt
**Effort**: Medium

---

## Phase 3: Power Features (Lower Priority)

### 3.1 Local LLM Inference (Borrow: Agora)
**What**: On-device GGUF model inference via llama.cpp
**How**:
- Add NDK/CMake build for llama.cpp
- JNI bridge for model loading/inference
- Model download manager
- Chat integration (select local model)
- GPU acceleration (Vulkan)
**Files**: new native/ module, ai/, settings
**Effort**: Very High

### 3.2 Conch Encryption (Borrow: Agora)
**What**: Application-layer encryption for API keys
**How**:
- Encrypt API keys at rest with user password
- Decrypt on demand
- Blank-key mode for HTTPS endpoints
**Files**: data/settings/, security/
**Effort**: Medium

### 3.3 Developer Mode (Borrow: Agora)
**What**: Advanced diagnostics and debugging
**How**:
- Unlock via tap pattern (7 taps on version)
- Diagnostics page: memory, network, DB stats
- Log viewer with filtering
- Performance metrics
- Crash reporter
**Files**: SettingsScreens.kt, new diagnostics/
**Effort**: Medium

### 3.4 Documentation (Borrow: Agora)
**What**: Comprehensive user manual
**How**:
- Create docs/ directory with Markdown
- MkDocs or similar for web hosting
- In-app help links
- Architecture guide for contributors
**Files**: docs/, README.md
**Effort**: Low-Medium

---

## Phase 4: Hybrid/Optional Features

### 4.1 Hybrid Sandbox (Lyra-Code Unique)
**What**: Multiple sandbox methods
**Options**:
- Termux (existing, full Linux environment)
- PRoot (Agora-style, bundled binaries)
- Remote SSH (existing)
- Docker (new, for advanced users)
- User chooses preferred method in settings
**Effort**: High

### 4.2 Hybrid Theme System
**What**: Combine curated presets + dynamic generation
**How**:
- Keep 5 curated dark presets as "quick themes"
- Add "Custom" tab with MaterialKolor seed picker
- Add "Import JSON" for full custom themes
- All three coexist in theme settings
**Effort**: Medium

### 4.3 Hybrid Context Management
**What**: Combine token budget + manual compact
**How**:
- Auto-compact when approaching budget
- Manual compact button for user control
- Show token usage indicator in chat
- Configurable: auto/manual/off
**Effort**: Medium

---

## Implementation Priority Matrix

| Feature | Impact | Effort | Priority |
|---|---|---|---|
| Dynamic themes (MaterialKolor) | High | Medium | 🔴 P1 |
| Token-budget compaction | High | High | 🔴 P1 |
| Message branching | High | High | 🟡 P2 |
| ChatGPT/Claude import | Medium | Medium | 🟡 P2 |
| Scheduled backups | Medium | Medium | 🟡 P2 |
| Multi-language | Medium | Medium | 🟡 P2 |
| PRoot sandbox | Medium | High | 🟢 P3 |
| Developer mode | Low | Medium | 🟢 P3 |
| Conch encryption | Low | Medium | 🟢 P3 |
| Local LLM | High | Very High | 🔵 P4 |
| Documentation | Low | Low | 🔵 P4 |

---

## What to Keep from Lyra-Code (Don't Replace)

1. **Curated dark theme presets** - users love these, keep as quick picks
2. **Profile customization** - unique feature, Agora doesn't have
3. **Hardcoded icon colors** - consistent visibility
4. **Settings grouping** - better than Agora's flat pages
5. **Email integration** - unique, Agora doesn't have
6. **WebDAV/FTP/SFTP** - unique, Agora doesn't have
7. **Mini server** - unique, Agora doesn't have
8. **Termux integration** - unique, more powerful than PRoot
9. **Dev APK coexistence** - useful for development
10. **Chat tool result cards** - better UX than Agora

---

## Migration Strategy

1. **Don't break existing features** - all changes are additive
2. **Feature flags** - new features behind settings toggles
3. **Gradual rollout** - implement one phase at a time
4. **CI verification** - build after each change
5. **User feedback** - test with real users before major changes

---

## Estimated Timeline

- Phase 1: 2-3 weeks
- Phase 2: 3-4 weeks  
- Phase 3: 4-6 weeks
- Phase 4: 6-8 weeks (local LLM is complex)

Total: ~4-6 months for full implementation
